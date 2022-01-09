package com.andy.aqs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2022/1/9
 * @version: 1.0.0
 */
class MyLock implements Lock {//自定义不可重入锁

    //借助自定义同步器类 实现自定义Lock
    static MyAQS myAQS = new MyAQS();

    @Override
    // 尝试，不成功，进入等待队列
    public void lock() {
        myAQS.acquire(1);
    }

    @Override
    // 尝试，不成功，进入等待队列，可打断
    public void lockInterruptibly() throws InterruptedException {
        myAQS.acquireInterruptibly(1);
    }

    @Override
    // 尝试一次，不成功返回，不进入队列
    public boolean tryLock() {
        return myAQS.tryAcquire(1);
    }

    @Override
    // 尝试，不成功，进入等待队列，有时限
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return myAQS.tryAcquireNanos(1, unit.toNanos(time));
    }

    @Override
    // 释放锁
    public void unlock() {
        myAQS.release(1);
    }

    @Override
    // 生成条件变量
    public Condition newCondition() {
        return myAQS.newCondition();
    }
}