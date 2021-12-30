package com.andy.communication;

import com.andy.utils.JUCUtils;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2021/12/23
 * @version: 1.0.0
 */
@Slf4j
public class HulaQuanTest {

    public static void main(String[] args) {
        //呼拉圈母题
        //三个线程轮流输出ABC，来5轮，A B C A B C A B C A B C A B C

        //synchronized-wait&notify方式
        SyncPrinter syncPrinter = new SyncPrinter();
        //设置启动点 从A开始
        syncPrinter.setFlag(1);
        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                syncPrinter.print("A", 1, 2);
            }
        }, "t1").start();

        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                syncPrinter.print("B", 2, 3);
            }
        }, "t2").start();

        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                syncPrinter.print("C", 3, 1);
            }
        }, "t3").start();

        //ReentrantLock-await&signal方式
        LockPrinter lockPrinter = new LockPrinter();
        ReentrantLock reentrantLock = lockPrinter.getReentrantLock();
        Condition conditionA = reentrantLock.newCondition();
        Condition conditionB = reentrantLock.newCondition();
        Condition conditionC = reentrantLock.newCondition();

        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                lockPrinter.print("A", conditionA, conditionB);
            }
        }, "t1").start();

        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                lockPrinter.print("B", conditionB, conditionC);
            }
        }, "t2").start();

        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                lockPrinter.print("C", conditionC, conditionA);
            }
        }, "t3").start();

        //触发启动 从A开始
        JUCUtils.sleep(2);
        reentrantLock.lock();
        try {
            conditionA.signalAll();
        } finally {
            reentrantLock.unlock();
        }

    }

}

class SyncPrinter {

    //交替标志位
    private int flag;

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public void print(String printStr, int runFlag, int nextFlag) {
        synchronized (this) {
            //不满足等待
            while (flag != runFlag) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //干活
            System.out.printf(printStr + " ");
            this.notifyAll();
            flag = nextFlag;
        }
    }

}

class LockPrinter {

    ReentrantLock reentrantLock = new ReentrantLock();

    public ReentrantLock getReentrantLock() {
        return reentrantLock;
    }

    public void print(String printStr, Condition runCondition, Condition nextCondition) {
        reentrantLock.lock();
        try {
            try {
                //等待直到被唤醒
                runCondition.await();

                //干活
                System.out.printf(printStr + " ");
                //唤醒下一个
                nextCondition.signalAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } finally {
            reentrantLock.unlock();
        }
    }

}
