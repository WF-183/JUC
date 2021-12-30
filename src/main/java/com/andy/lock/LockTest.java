package com.andy.lock;

import com.andy.utils.JUCUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2021/12/30
 * @version: 1.0.0
 */
@Slf4j
public class LockTest {

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        Thread t1 = new Thread(() -> {
            //尝试获取锁、尝试持续指定时间
            //if (!lock.tryLock(1, TimeUnit.SECONDS)) {
            if (!lock.tryLock()) {
                log.debug("获取失败，立刻返回");
                return;
            }
            try {
                log.debug("获取成功 dosomething");
            } finally {
                lock.unlock();
            }
        }, "t1");

        log.debug("主线程先占据锁 让t1抢不到");
        lock.lock();

        log.debug("启动t1");
        t1.start();
        try {
            JUCUtils.sleep(2);
        } finally {
            lock.unlock();
        }

    }
}
