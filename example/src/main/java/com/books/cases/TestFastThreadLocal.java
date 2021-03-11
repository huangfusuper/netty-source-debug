package com.books.cases;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.concurrent.FastThreadLocalThread;

import java.io.Serializable;

/**
 * *********************************************************************
 * TODO
 * *********************************************************************
 *
 * @author huangfu
 * @date 2021/2/26 22:40
 */
public class TestFastThreadLocal implements Serializable {

    private static final long serialVersionUID = -5881131553810653172L;

    public static void main(String[] args) {
        FastThreadLocal<String> fastThreadLocal = new FastThreadLocal<String>(){
            @Override
            protected String initialValue() throws Exception {
                return "huangfu";
            }
        };
        Thread thread = new FastThreadLocalThread(() -> {
            FastThreadLocal<String> fastThreadLocal1 = new FastThreadLocal<>();
            FastThreadLocal<String> fastThreadLocal2 = new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            new FastThreadLocal<>();
            while (true) {
                System.out.println(Thread.currentThread().getName()+"::::"+fastThreadLocal.get());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(fastThreadLocal.get());


                fastThreadLocal.set("thread000");
                fastThreadLocal1.set("thread1");
                fastThreadLocal2.set("thread1.....");
            }
        });
        thread.start();

        /**
         * 存在空间的浪费
         */
        new FastThreadLocalThread(() ->{
            FastThreadLocal<String> fastThreadLocal3 = new FastThreadLocal<>();
            fastThreadLocal3.set("12321321312");
        }).start();

    }
}
