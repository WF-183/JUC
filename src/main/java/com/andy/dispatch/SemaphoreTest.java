package com.andy.dispatch;

import com.andy.utils.JUCUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Semaphore;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2022/1/9
 * @version: 1.0.0
 */
@Slf4j
public class SemaphoreTest {//信号量

    public static void main(String[] args) {
        // 1. 创建 semaphore 对象
        Semaphore semaphore = new Semaphore(3);
        // 2. 10个线程同时运行，3个3个一批跑
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    // 3. 获取许可，获得通行，没有许可在此等待
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    log.debug("running...");
                    JUCUtils.sleep(1);
                    log.debug("end...");
                } finally {
                    // 4. 释放许可
                    semaphore.release();
                }
            }).start();
        }
    }
}
