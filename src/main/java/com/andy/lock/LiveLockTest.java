package com.andy.lock;

import com.andy.utils.JUCUtils;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2021/12/30
 * @version: 1.0.0
 */
@Slf4j
public class LiveLockTest {

    static volatile int count = 10;

    public static void main(String[] args) {
        new Thread(() -> {
            // 期望减到 0 退出循环
            while (count > 0) {
                JUCUtils.sleep(0.2);
                count--;
                log.debug("count: {}", count);
            }
        }, "t1").start();

        new Thread(() -> {
            // 期望超过 20 退出循环
            while (count < 20) {
                JUCUtils.sleep(0.2);
                count++;
                log.debug("count: {}", count);
            }
        }, "t2").start();
    }
}

