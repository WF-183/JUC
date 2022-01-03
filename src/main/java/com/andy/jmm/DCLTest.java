package com.andy.jmm;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2022/1/3
 * @version: 1.0.0
 */
public class DCLTest {//double check locking

}

public final class Singleton {
    private Singleton() {
    }

    private static Singleton INSTANCE = null;

    public static synchronized Singleton getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }
        INSTANCE = new Singleton();
        return INSTANCE;
    }
}

public final class Singleton {
    private Singleton() {
    }

    private static Singleton INSTANCE = null;

    public static Singleton getInstance() {
        if (INSTANCE == null) {
            synchronized (Singleton.class) {
                if (INSTANCE == null) {
                    INSTANCE = new Singleton();
                }
            }
        }
        return INSTANCE;
    }
}

public final class Singleton {
    private Singleton() {
    }

    private static volatile Singleton INSTANCE = null;

    public static Singleton getInstance() {
        // 实例没创建，才会进入内部的 synchronized代码块
        if (INSTANCE == null) {
            synchronized (Singleton.class) { // t2
                // 也许有其它线程已经创建实例，所以再判断一次
                if (INSTANCE == null) { // t1
                    INSTANCE = new Singleton();
                }
            }
        }
        return INSTANCE;
    }

}

public final class Singleton {

    private Singleton() { }
    private static volatile Singleton INSTANCE = null;

    public static Singleton getInstance() {
        //实例已创建，直接返回，不受加锁性能影响
        if (INSTANCE != null) {
            return INSTANCE;
        }
        //实例没创建，才会进入内部的 synchronized代码块
        synchronized (Singleton.class) {
            //这里的再次判断是必须的，防止两个以上线程同时通过第一个if，都等在锁这，那么第二个拿到锁的不判断就会再new一个
            if (INSTANCE != null) {
                return INSTANCE;
            }
            INSTANCE = new Singleton();
            return INSTANCE;
        }
    }
}
