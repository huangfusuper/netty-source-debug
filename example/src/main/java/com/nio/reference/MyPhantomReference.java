package com.nio.reference;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.nio.ByteBuffer;

/**
 * @author huangfu
 * @date
 */
public class MyPhantomReference {
    static ReferenceQueue<Object> queue = new ReferenceQueue<>();
    public static void main(String[] args) throws InterruptedException {
        byte[] bytes = new byte[10 * 1024];
        //将该对象被虚引用引用
        PhantomReference<Object> objectPhantomReference = new PhantomReference<Object>(bytes,queue);
        //这个一定返回null  因为实在接口定义中写死的
        System.out.println(objectPhantomReference.get());
        //此时jvm并没有进行对象的回收，该队列返回为空
        System.out.println(queue.poll());
        //手动释放该引用，将该引用置为无效引用
        bytes = null;
        //触发gc
        System.gc();
        //这里返回的还是null  接口定义中写死的
        System.out.println(objectPhantomReference.get());
        //垃圾回收后，被回收对象进入到引用队列
        System.out.println(queue.poll());
    }
}
