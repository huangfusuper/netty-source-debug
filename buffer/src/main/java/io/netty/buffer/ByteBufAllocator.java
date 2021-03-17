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

/**
 * Implementations are responsible to allocate buffers. Implementations of this interface are expected to be
 * thread-safe.
 */
public interface ByteBufAllocator {

    ByteBufAllocator DEFAULT = ByteBufUtil.DEFAULT_ALLOCATOR;

    /**
     * 分配一个{@link ByteBuf}。是直接缓冲区还是堆缓冲区取决于实际实现。
     */
    ByteBuf buffer();

    /**
     * 用给定的初始容量分配一个{@link ByteBuf}。是直接缓冲区还是堆缓冲区取决于实际实现。
     */
    ByteBuf buffer(int initialCapacity);

    /**
     *用给定的初始容量和给定的最大容量分配一个{@link ByteBuf}。是直接缓冲区还是堆缓冲区取决于实际实现。
     */
    ByteBuf buffer(int initialCapacity, int maxCapacity);

    /**
     * 分配一个{@link ByteBuf}，最好是一个适合IO的直接缓冲区。
     */
    ByteBuf ioBuffer();

    /**
     * 分配一个{@link ByteBuf}，最好是一个适合IO的直接缓冲区。
     */
    ByteBuf ioBuffer(int initialCapacity);

    /**
     * 分配一个{@link ByteBuf}，最好是一个适合IO的直接缓冲区。
     */
    ByteBuf ioBuffer(int initialCapacity, int maxCapacity);

    /**
     * 分配堆{@link ByteBuf}。
     */
    ByteBuf heapBuffer();

    /**
     * 用给定的初始容量分配堆{@link ByteBuf}。
     */
    ByteBuf heapBuffer(int initialCapacity);

    /**
     * 用给定的初始容量和给定的最大容量分配堆{@link ByteBuf}。
     */
    ByteBuf heapBuffer(int initialCapacity, int maxCapacity);

    /**
     *分配直接的{@link ByteBuf}。
     */
    ByteBuf directBuffer();

    /**
     * 用给定的初始容量分配直接的{@link ByteBuf}。
     */
    ByteBuf directBuffer(int initialCapacity);

    /**
     * 分配具有给定初始容量和给定最大容量的直接{@link ByteBuf}。
     */
    ByteBuf directBuffer(int initialCapacity, int maxCapacity);

    /**
     * 分配一个{@link CompositeByteBuf}。是直接缓冲区还是堆缓冲区取决于实际实现。
     */
    CompositeByteBuf compositeBuffer();

    /**
     * 分配一个{@link CompositeByteBuf}，其中包含可以存储的给定最大组件数。是直接缓冲区还是堆缓冲区取决于实际实现。
     */
    CompositeByteBuf compositeBuffer(int maxNumComponents);

    /**
     * 分配堆{@link CompositeByteBuf}。
     */
    CompositeByteBuf compositeHeapBuffer();

    /**
     * 分配给堆{@link CompositeByteBuf}，其中包含给定的最大可以存储在其中的组件数量。
     */
    CompositeByteBuf compositeHeapBuffer(int maxNumComponents);

    /**
     * 分配直接的{@link CompositeByteBuf}。
     */
    CompositeByteBuf compositeDirectBuffer();

    /**
     * 分配一个直接的{@link CompositeByteBuf}，其中包含可以存储的给定最大组件数。
     */
    CompositeByteBuf compositeDirectBuffer(int maxNumComponents);

    /**
     * 如果直接的{@link ByteBuf}被池化，则返回{@code true}
     */
    boolean isDirectBufferPooled();

    /**
     *计算当{@link ByteBuf}需要扩展{@code minNewCapacity}且上限为{@code maxCapacity}时使用的{@link ByteBuf}的新容量。
     */
    int calculateNewCapacity(int minNewCapacity, int maxCapacity);
 }
