package com.andy.thread;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2021/12/21
 * @version: 1.0.0
 */
@Slf4j
public class InterruptTest {

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {
            while (true) {
                log.info("t1在无限循环中");
                if (Thread.currentThread().isInterrupted()) {
                    log.debug("运行中的线程收到打断，自定义行为：被打断则结束循环");
                    break;
                }
            }
        }, "t1");
        t1.start();

        sleep(1);
        t1.interrupt();



    }


    private static void sleep(int seconds){
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class InterruptStop {
    private Thread thread;

    public void start() {
        thread = new Thread(() -> {
            while (true) {
                Thread current = Thread.currentThread();
                if (current.isInterrupted()) {
                    //料理后事

                    //结束线程
                    break;
                }
                try {
                    Thread.sleep(2000);
                    //执行工作
                } catch (InterruptedException e) {
                    //sleep期间被打断，标志位不会改，手动set true
                    current.interrupt();
                }
            }
        }, "监控线程");
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }
}

// 停止标记用 volatile 是为了保证该变量在多个线程之间的可见性
// 我们的例子中，即主线程把它修改为 true 对 t1 线程可见
class InterruptVolatile {



    private Thread thread;
    private volatile boolean stop = false;

    //Thread.State

    public void start() {
        thread = new Thread(() -> {
            while (true) {
                Thread current = Thread.currentThread();
                if (stop) {
                    //料理后事
                    //结束线程
                    break;
                }
                try {
                    Thread.sleep(1000);
                    //执行工作
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "监控线程");
        thread.start();
    }

    public void stop() {
        stop = true;
    }
}