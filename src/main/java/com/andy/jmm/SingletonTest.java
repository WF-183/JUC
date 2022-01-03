package com.andy.jmm;

import java.io.Serializable;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2022/1/3
 * @version: 1.0.0
 */
public class SingletonTest {//单例模式


}

//饿汉式单例
// 问题1:为什么加final ：防止存在子类，覆写方法破坏单例逻辑
public final class Singleton implements Serializable {

    //问题3:为什么设置为私有?  ： 防止随意new对象
    //是否能防止反射创建新的实例? ： 不能，暴力反射很强大
    private Singleton() {
    }

    // 问题4:这样初始化是否能保证单例对象创建时的线程安全? ：可以，静态成员变量是在类加载时创建，jvm会保证类加载期间的线程安全问题
    private static final Singleton INSTANCE = new Singleton();

    // 问题5:为什么提供静态方法而不是直接将 INSTANCE 设置为 public, 说出你知道的理由
    //1、封装性更好 2、方便以后改成懒汉式加载
    public static Singleton getInstance() {
        return INSTANCE;
    }

    //问题2:如果实现了序列化接口, 怎么防止反序列化破坏单例
    //类中加一个readResolve方法，反序列化时将按约定使用这个对象，不会创建新对象
    public Object readResolve() {
        return INSTANCE;
    }
}

//懒汉式单例
//懒加载+没安全问题+没性能问题，这就是一个完美的懒汉式单例写法
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


//枚举单例
// 问题1:枚举单例是如何限制实例个数的 ：限制成员变量个数即可
// 问题5:枚举单例属于懒汉式还是饿汉式 ：饿汉式
// 问题2:枚举单例在创建时是否有并发问题：没有，静态成员变量，在类加载时已创建
// 问题3:枚举单例能否被反射破坏单例 ： 不能，这是枚举单例的好处
// 问题4:枚举单例能否被反序列化破坏单例 ：不能，这是枚举单例的好处，所有枚举类默认实现类序列化接口，自身已做处理，防止了这个问题
enum Singleton {
    INSTANCE;
}

//静态内部类单例
public final class Singleton {
    private Singleton() {
    }

    // 问题1:属于懒汉式还是饿汉式 ： 饿汉式，spring类加载本身就是懒加载的
    private static class LazyHolder {
        static final Singleton INSTANCE = new Singleton();
    }

    // 问题2:在创建时是否有并发问题 ：没有，静态成员变量是在类加载时创建，jvm会保证类加载期间的线程安全问题，请相信jvm
    public static Singleton getInstance() {
        return LazyHolder.INSTANCE;
    }
}