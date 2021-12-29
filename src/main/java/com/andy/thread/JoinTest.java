package com.andy.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2021/12/21
 * @version: 1.0.0
 */
@Slf4j
public class JoinTest {

    static int r1 = 0;
    static int r2 = 0;

    public static void main(String[] args) throws InterruptedException {
        //test2();

    }

    private static void test2() throws InterruptedException {
        Thread t1 = new Thread(() -> {
            sleep(1);
            r1 = 10;
        });
        Thread t2 = new Thread(() -> {
            sleep(2);
            r2 = 20;
        });
        long start = System.currentTimeMillis();
        t1.start();
        t2.start();

        //同步阻塞2s
        t2.join();
        //t1内容已结束，瞬间过去
        t1.join();
        long end = System.currentTimeMillis();
        //r1: 10 r2: 20 cost: 2005
        log.debug("r1: {} r2: {} cost: {}", r1, r2, end - start);
    }


    private static void sleep(int seconds){
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
