package com.andy.lock;

import com.andy.utils.JUCUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2021/12/30
 * @version: 1.0.0
 */
@Slf4j
public class DeadLockTest {

    public static void main(String[] args) {
        ////死锁简单case
        //Object A = new Object();
        //Object B = new Object();
        //Thread t1 = new Thread(() -> {
        //    synchronized (A) {
        //        log.debug("lock A");
        //        JUCUtils.sleep(2);
        //        synchronized (B) {
        //            log.debug("lock B");
        //            log.debug("操作...");
        //        }
        //    }
        //}, "t1");
        //Thread t2 = new Thread(() -> {
        //    synchronized (B) {
        //        log.debug("lock B");
        //        JUCUtils.sleep(1);
        //        synchronized (A) {
        //            log.debug("lock A");
        //            log.debug("操作...");
        //        }
        //    }
        //}, "t2");
        //t1.start();
        //t2.start();

        //哲学家就餐死锁问题
        //有五位哲学家，围坐在圆桌，两两之间有一根筷子，一共五个人五根筷子，吃饭必须要用两根筷子吃，每个人都不断尝试吃饭操作，死锁。
        Chopstick c1 = new Chopstick("1");
        Chopstick c2 = new Chopstick("2");
        Chopstick c3 = new Chopstick("3");
        Chopstick c4 = new Chopstick("4");
        Chopstick c5 = new Chopstick("5");
        new Philosopher("苏格拉底", c1, c2).start();
        new Philosopher("柏拉图", c2, c3).start();
        new Philosopher("亚里士多德", c3, c4).start();
        new Philosopher("赫拉克利特", c4, c5).start();
        new Philosopher("阿基米德", c5, c1).start();
    }

}

//筷子类
class Chopstick extends ReentrantLock {
    String id;

    public Chopstick(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "筷子{" + id + '}';
    }
}

//哲学家类
@Slf4j
class Philosopher extends Thread {
    Chopstick left;
    Chopstick right;

    public Philosopher(String name, Chopstick left, Chopstick right) {
        super(name);
        this.left = left;
        this.right = right;
    }

    private void eat() {
        log.debug("eating...");
        JUCUtils.sleep(1);
    }

    @Override
    public void run() {
        //解决死锁写法 使用ReentrantLock.tryLock()解决死锁问题
        while (true) {
            // 尝试获得左手筷子
            if (left.tryLock()) {
                try {
                    // 尝试获得右手筷子
                    if (right.tryLock()) {
                        try {
                            eat();
                        } finally {
                            right.unlock();
                        }
                    }
                } finally {
                    left.unlock();
                }
            }
        }

        //死锁写法
        //while (true) {
        //    // 获得左手筷子
        //    synchronized (left) {
        //        // 获得右手筷子
        //        synchronized (right) {
        //            // 吃饭
        //            eat();
        //        }// 放下右手筷子
        //    }// 放下左手筷子
        //}

    }
}
