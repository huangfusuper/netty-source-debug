/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.netty.buffer;

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Description of algorithm for PageRun/PoolSubpage allocation from PoolChunk
 *
 * Notation: The following terms are important to understand the code
 * > page  - a page is the smallest unit of memory chunk that can be allocated
 * > chunk - a chunk is a collection of pages
 * > in this code chunkSize = 2^{maxOrder} * pageSize
 *
 * To begin we allocate a byte array of size = chunkSize
 * Whenever a ByteBuf of given size needs to be created we search for the first position
 * in the byte array that has enough empty space to accommodate the requested size and
 * return a (long) handle that encodes this offset information, (this memory segment is then
 * marked as reserved so it is always used by exactly one ByteBuf and no more)
 *
 * For simplicity all sizes are normalized according to PoolArena#normalizeCapacity method
 * This ensures that when we request for memory segments of size >= pageSize the normalizedCapacity
 * equals the next nearest power of 2
 *
 * To search for the first offset in chunk that has at least requested size available we construct a
 * complete balanced binary tree and store it in an array (just like heaps) - memoryMap
 *
 * The tree looks like this (the size of each node being mentioned in the parenthesis)
 *
 * depth=0        1 node (chunkSize)
 * depth=1        2 nodes (chunkSize/2)
 * ..
 * ..
 * depth=d        2^d nodes (chunkSize/2^d)
 * ..
 * depth=maxOrder 2^maxOrder nodes (chunkSize/2^{maxOrder} = pageSize)
 *
 * depth=maxOrder is the last level and the leafs consist of pages
 *
 * With this tree available searching in chunkArray translates like this:
 * To allocate a memory segment of size chunkSize/2^k we search for the first node (from left) at height k
 * which is unused
 *
 * Algorithm:
 * ----------
 * Encode the tree in memoryMap with the notation
 *   memoryMap[id] = x => in the subtree rooted at id, the first node that is free to be allocated
 *   is at depth x (counted from depth=0) i.e., at depths [depth_of_id, x), there is no node that is free
 *
 *  As we allocate & free nodes, we update values stored in memoryMap so that the property is maintained
 *
 * Initialization -
 *   In the beginning we construct the memoryMap array by storing the depth of a node at each node
 *     i.e., memoryMap[id] = depth_of_id
 *
 * Observations:
 * -------------
 * 1) memoryMap[id] = depth_of_id  => it is free / unallocated
 * 2) memoryMap[id] > depth_of_id  => at least one of its child nodes is allocated, so we cannot allocate it, but
 *                                    some of its children can still be allocated based on their availability
 * 3) memoryMap[id] = maxOrder + 1 => the node is fully allocated & thus none of its children can be allocated, it
 *                                    is thus marked as unusable
 *
 * Algorithm: [allocateNode(d) => we want to find the first node (from left) at height h that can be allocated]
 * ----------
 * 1) start at root (i.e., depth = 0 or id = 1)
 * 2) if memoryMap[1] > d => cannot be allocated from this chunk
 * 3) if left node value <= h; we can allocate from left subtree so move to left and repeat until found
 * 4) else try in right subtree
 *
 * Algorithm: [allocateRun(size)]
 * ----------
 * 1) Compute d = log_2(chunkSize/size)
 * 2) Return allocateNode(d)
 *
 * Algorithm: [allocateSubpage(size)]
 * ----------
 * 1) use allocateNode(maxOrder) to find an empty (i.e., unused) leaf (i.e., page)
 * 2) use this handle to construct the PoolSubpage object or if it already exists just call init(normCapacity)
 *    note that this PoolSubpage object is added to subpagesPool in the PoolArena when we init() it
 *
 * Note:
 * -----
 * In the implementation for improving cache coherence,
 * we store 2 pieces of information depth_of_id and x as two byte values in memoryMap and depthMap respectively
 *
 * memoryMap[id]= depth_of_id  is defined above
 * depthMap[id]= x  indicates that the first node which is free to be allocated is at depth x (from root)
 */
final class PoolChunk<T> implements PoolChunkMetric {

    private static final int INTEGER_SIZE_MINUS_ONE = Integer.SIZE - 1;
    //表示该PoolChunk所属的PoolArena。
    final PoolArena<T> arena;
    // 具体用来表示内存；byte[]或java.nio.ByteBuffer。
    final T memory;
    // 是否是可重用的，unpooled=false表示可重用
    final boolean unpooled;
    final int offset;
    //记录二叉树结点的分配信息
    private final byte[] memoryMap;
    //存放节点所对应的高度
    private final byte[] depthMap;
    //表示该PoolChunk所包含的PoolSubpage。也就是PoolChunk连续的可用内存。
    //// PoolChunk 中管理的 2048 个 8K 内存块
    private final PoolSubpage<T>[] subpages;
    /** 用于确定请求的容量是否等于或大于pageSize。 */
    private final int subpageOverflowMask;
    //每个PoolSubpage的大小，默认为8192个字节（8K)
    private final int pageSize;
    private final int pageShifts;
    //二叉树深度
    private final int maxOrder;
    private final int chunkSize;
    private final int log2ChunkSize;
    private final int maxSubpageAllocs;
    /** 用于将内存标记为不可用 */
    private final byte unusable;

    // Use as cache for ByteBuffer created from the memory. These are just duplicates and so are only a container
    // around the memory itself. These are often needed for operations within the Pooled*ByteBuf and so
    // may produce extra GC, which can be greatly reduced by caching the duplicates.
    //
    // 如果将PoolChunk取消池化，则此值可能为null，因为在这里合并ByteBuffer实例没有任何意义。
    private final Deque<ByteBuffer> cachedNioBuffers;
    //当前PoolChunk空闲的内存。
    private int freeBytes;

    PoolChunkList<T> parent;
    //前置 后置
    PoolChunk<T> prev;
    PoolChunk<T> next;

    // TODO: Test if adding padding helps under contention
    //private long pad0, pad1, pad2, pad3, pad4, pad5, pad6, pad7;  一个 chunkSize是16M

    PoolChunk(PoolArena<T> arena, T memory, int pageSize, int maxOrder, int pageShifts, int chunkSize, int offset) {
        unpooled = false;
        this.arena = arena;
        this.memory = memory;
        this.pageSize = pageSize;
        this.pageShifts = pageShifts;
        //默认11   maxSubpageAllocs = 1 << maxOrder
        //可得一个PoolChunk默认情况下由2^11=2048个SubPage构成，而默认情况下一个page默认大小为8k，即pageSize=8K。
        this.maxOrder = maxOrder;
        this.chunkSize = chunkSize;
        this.offset = offset;
        unusable = (byte) (maxOrder + 1);
        log2ChunkSize = log2(chunkSize);
        subpageOverflowMask = ~(pageSize - 1);
        freeBytes = chunkSize;

        assert maxOrder < 30 : "maxOrder should be < 30, but is: " + maxOrder;
        maxSubpageAllocs = 1 << maxOrder; //右移11层
        // 生成内存映射。
        //PoolChunk中所有的PoolSubpage都放在PoolSubpage[] subpages中，为了更好的分配，Netty用一颗平衡二叉树记录每个PoolSubpage的分配情况   2048
        memoryMap = new byte[maxSubpageAllocs << 1];//已使用的内存  11层 满二叉树为4096个节点
        depthMap = new byte[memoryMap.length];//节点深度
        //开始构建一个平衡二叉树
        int memoryMapIndex = 1;
        // 一次向下移动树一级  maxOrder 树的最大层级
        for (int d = 0; d <= maxOrder; ++ d) {
            //深度 计算深度
            int depth = 1 << d;
            //memoryMap={第0位没用到,0,1,1,2,2,2,2,3,3,3,3,3,3,3,3,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4},//memoryMap数组元素长度为 {(1<<maxOrder)>>1}}=32
            for (int p = 0; p < depth; ++ p) {
                // 在每个级别中从左到右遍历并将值设置为子树的深度
                memoryMap[memoryMapIndex] = (byte) d;
                depthMap[memoryMapIndex] = (byte) d;
                memoryMapIndex ++;
            }
        }
        //创建 PollSubpage
        subpages = newSubpageArray(maxSubpageAllocs);
        cachedNioBuffers = new ArrayDeque<ByteBuffer>(8);
    }

    /** Creates a special chunk that is not pooled. */
    PoolChunk(PoolArena<T> arena, T memory, int size, int offset) {
        unpooled = true;
        this.arena = arena;
        this.memory = memory;
        this.offset = offset;
        memoryMap = null;
        depthMap = null;
        subpages = null;
        subpageOverflowMask = 0;
        pageSize = 0;
        pageShifts = 0;
        maxOrder = 0;
        unusable = (byte) (maxOrder + 1);
        chunkSize = size;
        log2ChunkSize = log2(chunkSize);
        maxSubpageAllocs = 0;
        cachedNioBuffers = null;
    }

    @SuppressWarnings("unchecked")
    private PoolSubpage<T>[] newSubpageArray(int size) {
        return new PoolSubpage[size];
    }

    @Override
    public int usage() {
        final int freeBytes;
        synchronized (arena) {
            freeBytes = this.freeBytes;
        }
        return usage(freeBytes);
    }

    private int usage(int freeBytes) {
        if (freeBytes == 0) {
            return 100;
        }

        int freePercentage = (int) (freeBytes * 100L / chunkSize);
        if (freePercentage == 0) {
            return 99;
        }
        return 100 - freePercentage;
    }

    /**
     * 查看是佛能够分配成功
     * @param buf 缓存
     * @param reqCapacity 需求容量
     * @param normCapacity 规范大小   8k
     * @return
     */
    boolean allocate(PooledByteBuf<T> buf, int reqCapacity, int normCapacity) {
        final long handle;
        // >= pageSize   大于等于8k
        if ((normCapacity & subpageOverflowMask) != 0) {
            //返回分配的节点的id   平衡二叉树中的空闲的节点id
            handle =  allocateRun(normCapacity);
        } else {
            //subpage
            handle = allocateSubpage(normCapacity);
        }
        //没有分配成功
        if (handle < 0) {
            return false;
        }
        ByteBuffer nioBuffer = cachedNioBuffers != null ? cachedNioBuffers.pollLast() : null;
        initBuf(buf, nioBuffer, handle, reqCapacity);  //初始化内存
        return true;
    }

    /**
     * 分配使用的更新方法
     * 仅当分配了后继者并且其所有前任者需要更新其状态时才触发此操作。
     * 以id为根的子树具有一些可用空间的最小深度
     *
     * 逐级向上修改  上级修改的值以子节点的最小值为值
     *
     * @param id id
     */
    private void updateParentsAlloc(int id) {
        while (id > 1) {
            int parentId = id >>> 1;
            byte val1 = value(id);
            byte val2 = value(id ^ 1);
            byte val = val1 < val2 ? val1 : val2;
            setValue(parentId, val);
            id = parentId;
        }
    }

    /**
     * Update method used by free
     * This needs to handle the special case when both children are completely free
     * in which case parent be directly allocated on request of size = child-size * 2
     *
     * @param id id
     */
    private void updateParentsFree(int id) {
        int logChild = depth(id) + 1;
        while (id > 1) {
            int parentId = id >>> 1;
            byte val1 = value(id);
            byte val2 = value(id ^ 1);
            logChild -= 1; // in first iteration equals log, subsequently reduce 1 from logChild as we traverse up

            if (val1 == logChild && val2 == logChild) {
                setValue(parentId, (byte) (logChild - 1));
            } else {
                byte val = val1 < val2 ? val1 : val2;
                setValue(parentId, val);
            }

            id = parentId;
        }
    }

    /**
     * 查询深度d的空闲节点时在memoryMap中分配索引的算法
     *
     * @param d 深度
     * @return memoryMap中的索引
     */
    private int allocateNode(int d) {
        int id = 1;
        int initial = - (1 << d); // 最后d位= 0，其余全部= 1
        byte val = value(id);
        if (val > d) { // 无法使用
            return -1;
        }
        //开始遍历平衡二叉树寻找可用的节点
        while (val < d || (id & initial) == 0) { // id＆initial == 1 << d对于深度d的所有id，对于<d为0
            id <<= 1;
            val = value(id);
            //这里注意父级节点的状态的修改是取的子节点的最小值，当子节点全部分配完毕之后，父节点也会变，就会进入到判断逻辑里面
            if (val > d) {
                //如果寻找到的节点分配过了，这里会向后移动一位
                id ^= 1;
                val = value(id);
            }
        }
        //寻找到了
        byte value = value(id);
        assert value == d && (id & initial) == 1 << d : String.format("val = %d, id & initial = %d, d = %d",
                value, id & initial, d);
        setValue(id, unusable); // 标记为不可用
        //修改父级节点
        updateParentsAlloc(id);
        return id;
    }

    /**
     * 分配页面 (>=1)
     *
     * @param normCapacity 归一化容量
     * @return memoryMap中的索引
     */
    private long allocateRun(int normCapacity) {
        //计算平衡二叉树的深度  对应分配内存大小的二叉树深度
        int d = maxOrder - (log2(normCapacity) - pageShifts);
        //开始给该需求容量分配一个节点  查询到节点
        int id = allocateNode(d);
        //没有分配成功
        if (id < 0) {
            return id;
        }
        //空闲内存-刚刚分配的内存   16M - 分配的规范内存
        freeBytes -= runLength(id);
        //这里返回的id一定是平衡二叉树中未使用的内存空间
        return id;
    }

    /**
     * Create / initialize a new PoolSubpage of normCapacity
     * Any PoolSubpage created / initialized here is added to subpage pool in the PoolArena that owns this PoolChunk
     *
     * @param normCapacity normalized capacity
     * @return index in memoryMap
     */
    private long allocateSubpage(int normCapacity) {
        //获取PoolArena拥有的PoolSubPage池的头部，并在其上进行同步。
        // 这是必需的，因为我们可能会重新添加它，从而更改链表的结构。
        PoolSubpage<T> head = arena.findSubpagePoolHead(normCapacity);
        // 子页面只能从页面分配，即离开
        int d = maxOrder;
        synchronized (head) {
            //
            int id = allocateNode(d);
            if (id < 0) {
                return id;
            }
            //记录哪些 Page 被转化为 Subpage
            final PoolSubpage<T>[] subpages = this.subpages;
            final int pageSize = this.pageSize;

            freeBytes -= pageSize;
            //计算 pageID对应subpage的Id   2048对应0   2049对应1   2049 ^ 2048 = 1
            int subpageIdx = subpageIdx(id);
            PoolSubpage<T> subpage = subpages[subpageIdx];
            if (subpage == null) {
                // 创建 PoolSubpage，并切分为相同大小的子内存块，然后加入 PoolArena 对应的双向链表中
                subpage = new PoolSubpage<T>(head, this, id, runOffset(id), pageSize, normCapacity);
                subpages[subpageIdx] = subpage;
            } else {
                subpage.init(head, normCapacity);
            }
            return subpage.allocate();
        }
    }

    /**
     * Free a subpage or a run of pages
     * When a subpage is freed from PoolSubpage, it might be added back to subpage pool of the owning PoolArena
     * If the subpage pool in PoolArena has at least one other PoolSubpage of given elemSize, we can
     * completely free the owning Page so it is available for subsequent allocations
     *
     * @param handle handle to free
     */
    void free(long handle, ByteBuffer nioBuffer) {
        int memoryMapIdx = memoryMapIdx(handle);
        int bitmapIdx = bitmapIdx(handle);

        if (bitmapIdx != 0) { // free a subpage
            PoolSubpage<T> subpage = subpages[subpageIdx(memoryMapIdx)];
            assert subpage != null && subpage.doNotDestroy;

            // Obtain the head of the PoolSubPage pool that is owned by the PoolArena and synchronize on it.
            // This is need as we may add it back and so alter the linked-list structure.
            PoolSubpage<T> head = arena.findSubpagePoolHead(subpage.elemSize);
            synchronized (head) {
                if (subpage.free(head, bitmapIdx & 0x3FFFFFFF)) {
                    return;
                }
            }
        }
        freeBytes += runLength(memoryMapIdx);
        setValue(memoryMapIdx, depth(memoryMapIdx));
        updateParentsFree(memoryMapIdx);

        if (nioBuffer != null && cachedNioBuffers != null &&
                cachedNioBuffers.size() < PooledByteBufAllocator.DEFAULT_MAX_CACHED_BYTEBUFFERS_PER_CHUNK) {
            cachedNioBuffers.offer(nioBuffer);
        }
    }

    void initBuf(PooledByteBuf<T> buf, ByteBuffer nioBuffer, long handle, int reqCapacity) {
        //再平衡二叉树上找的节点id
        int memoryMapIdx = memoryMapIdx(handle);
        int bitmapIdx = bitmapIdx(handle);
        if (bitmapIdx == 0) {
            //查询是否为不可用  不可用就对了
            byte val = value(memoryMapIdx);
            assert val == unusable : String.valueOf(val);
            buf.init(this, nioBuffer, handle, runOffset(memoryMapIdx) + offset,
                    reqCapacity, runLength(memoryMapIdx), arena.parent.threadCache());
        } else {
            initBufWithSubpage(buf, nioBuffer, handle, bitmapIdx, reqCapacity);
        }
    }

    void initBufWithSubpage(PooledByteBuf<T> buf, ByteBuffer nioBuffer, long handle, int reqCapacity) {
        initBufWithSubpage(buf, nioBuffer, handle, bitmapIdx(handle), reqCapacity);
    }

    private void initBufWithSubpage(PooledByteBuf<T> buf, ByteBuffer nioBuffer,
                                    long handle, int bitmapIdx, int reqCapacity) {
        assert bitmapIdx != 0;

        int memoryMapIdx = memoryMapIdx(handle);

        PoolSubpage<T> subpage = subpages[subpageIdx(memoryMapIdx)];
        assert subpage.doNotDestroy;
        assert reqCapacity <= subpage.elemSize;

        buf.init(
            this, nioBuffer, handle,
            runOffset(memoryMapIdx) + (bitmapIdx & 0x3FFFFFFF) * subpage.elemSize + offset,
                reqCapacity, subpage.elemSize, arena.parent.threadCache());
    }

    private byte value(int id) {
        return memoryMap[id];
    }

    private void setValue(int id, byte val) {
        memoryMap[id] = val;
    }

    private byte depth(int id) {
        return depthMap[id];
    }

    private static int log2(int val) {
        // 计算最高设置位的位置（从0开始，lsb = 0），即log2
        return INTEGER_SIZE_MINUS_ONE - Integer.numberOfLeadingZeros(val);
    }

    private int runLength(int id) {
        // 表示树中节点“ id”支持的大小（以字节为单位）
        return 1 << log2ChunkSize - depth(id);
    }

    private int runOffset(int id) {
        // 表示从字节数组块开始的字节中从0开始的偏移量
        int shift = id ^ 1 << depth(id);
        return shift * runLength(id);
    }

    private int subpageIdx(int memoryMapIdx) {
        return memoryMapIdx ^ maxSubpageAllocs; // 删除最高设置位以获得偏移量
    }
    private static int memoryMapIdx(long handle) {
        return (int) handle;
    }

    private static int bitmapIdx(long handle) {
        return (int) (handle >>> Integer.SIZE); //2048 >>> 32   用于表示二进制补码二进制形式的int值的位数
    }

    @Override
    public int chunkSize() {
        return chunkSize;
    }

    @Override
    public int freeBytes() {
        synchronized (arena) {
            return freeBytes;
        }
    }

    @Override
    public String toString() {
        final int freeBytes;
        synchronized (arena) {
            freeBytes = this.freeBytes;
        }

        return new StringBuilder()
                .append("Chunk(")
                .append(Integer.toHexString(System.identityHashCode(this)))
                .append(": ")
                .append(usage(freeBytes))
                .append("%, ")
                .append(chunkSize - freeBytes)
                .append('/')
                .append(chunkSize)
                .append(')')
                .toString();
    }

    void destroy() {
        arena.destroyChunk(this);
    }
}
