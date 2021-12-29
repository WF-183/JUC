package com.andy.lock;

import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2021/12/23
 * @version: 1.0.0
 */
@Slf4j
public class SynchronizedTest {


    static int counter = 0;
    static final Object room = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                synchronized (room) {
                    counter++;
                }
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5000; i++) {
                synchronized (room) {
                    counter--;
                }
            }
        }, "t2");
        t1.start();
        t2.start();

        t1.join();
        t2.join();
        log.debug("{}", counter);
    }


}
