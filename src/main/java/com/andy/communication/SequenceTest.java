package com.andy.communication;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2021/12/30
 * @version: 1.0.0
 */
public class SequenceTest {


    //t2运行标记， 代表t2是否执行过
    static boolean t2runed = false;
    static Object obj = new Object();

    public static void main(String[] args) {
        Thread t1 = new Thread(() -> {
            synchronized (obj) {
                // 如果 t2 没有执行过
                while (!t2runed) {
                    try {
                        // t1 先等一会
                        obj.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            //再dosomething
            System.out.println(1);
        });

        Thread t2 = new Thread(() -> {
            //先dosomething
            System.out.println(2);
            synchronized (obj) {
                // 修改运行标记
                t2runed = true;
                // 通知 obj 上等待的线程(可能有多个，因此需要用 notifyAll)
                obj.notifyAll();
            }
        });
        t1.start();
        t2.start();
    }
}
