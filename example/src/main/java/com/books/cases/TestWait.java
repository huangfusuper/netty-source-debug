package com.books.cases;

/**
 * @author huangfu
 * @date
 */
public class TestWait {
    public void sync(){
        synchronized (this) {
            try {
                wait();
                System.out.println("-------线程释放锁，进入锁池---------");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    public void success(){
        synchronized (this) {
            System.out.println("-------开始通知锁池数据，开似乎进行抢占");
            notifyAll();
        }

    }


    public static void main(String[] args) throws InterruptedException {
        TestWait testWait = new TestWait();
        Thread thread1 = new Thread(() ->{
            testWait.sync();
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread1.start();
        Thread.sleep(10000);
        testWait.success();
    }
}
