package com.andy.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2022/1/9
 * @version: 1.0.0
 */
@Slf4j
public class AqsTest {

    //private static MyLock myLock = new MyLock();//不可重入锁 自定义实现，进不去methed2
    private static ReentrantLock myLock = new ReentrantLock();//可重入锁，能进去methed2

    public static void main(String[] args) {

        new Thread(() -> {
            method1();
        }, "t1").start();

    }

    public static void method1() {
        myLock.lock();
        try {
            log.debug("execute method1");
            method2();
        } finally {
            myLock.unlock();
        }
    }

    public static void method2() {
        myLock.lock();
        try {
            log.debug("execute method2");
        } finally {
            myLock.unlock();
        }
    }
}
