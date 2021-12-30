package com.andy.communication;

import com.andy.utils.JUCUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2021/12/30
 * @version: 1.0.0
 */
@Slf4j
public class AwaitSignalTest {

    //烟
    static boolean hasCigarette = false;
    //外卖
    static boolean hasTakeout = false;

    static ReentrantLock reentrantLock = new ReentrantLock();
    static Condition cigaretteWaitsetCondition = reentrantLock.newCondition();
    static Condition takeoutWaitsetCondition = reentrantLock.newCondition();

    public static void main(String[] args) {
        new Thread(() -> {
            reentrantLock.lock();
            try {
                log.debug("有烟没?[{}]", hasCigarette);
                while (!hasCigarette) {
                    log.debug("没烟，先歇会!");
                    try {
                        cigaretteWaitsetCondition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //干活
                log.debug("烟到了立刻干活");
            } finally {
                reentrantLock.unlock();
            }
        }, "小南").start();

        new Thread(() -> {
            reentrantLock.lock();
            try {
                log.debug("外卖送到没?[{}]", hasTakeout);
                while (!hasTakeout) {
                    log.debug("没外卖，先歇会!");
                    try {
                        takeoutWaitsetCondition.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //干活
                log.debug("外卖到了立刻干活");
            } finally {
                reentrantLock.unlock();
            }
        }, "小女").start();

        JUCUtils.sleep(2);
        new Thread(() -> {
            reentrantLock.lock();
            try {
                hasTakeout = true;
                log.debug("外卖到了噢!");
                takeoutWaitsetCondition.signalAll();
            } finally {
                reentrantLock.unlock();
            }
        }, "送外卖的").start();

        JUCUtils.sleep(2);
        new Thread(() -> {
            reentrantLock.lock();
            try {
                hasCigarette = true;
                log.debug("烟到了噢!");
                cigaretteWaitsetCondition.signalAll();
            } finally {
                reentrantLock.unlock();
            }
        }, "送烟的").start();
    }

}
