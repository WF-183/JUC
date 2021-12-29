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
public class SleepTest {

    public static void main(String[] args) {
        Thread t1 = new Thread("t1") {
            @Override
            public void run() {
                try {
                    log.info("子线程 进入sleep");
                    Thread.sleep(2000L);
                } catch (InterruptedException e) {
                    log.info("子线程 被打断唤醒，抛出指定异常");
                    e.printStackTrace();
                }

            }
        };
        t1.start();

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("主线程 打断指定子线程sleep");
        t1.interrupt();


        //可读性高点
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
