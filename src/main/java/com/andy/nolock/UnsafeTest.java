package com.andy.nolock;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2022/1/5
 * @version: 1.0.0
 */
public class UnsafeTest {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {

        //基本使用
        //反射获取Unsafe
        Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
        theUnsafe.setAccessible(true);
        Unsafe unsafe = (Unsafe)theUnsafe.get(null);
        //获得成员变量的偏移量，后面方法需要这个作为传参
        Field id = Teacher.class.getDeclaredField("id");
        Field name = Teacher.class.getDeclaredField("name");
        long idOffset = unsafe.objectFieldOffset(id);
        long nameOffset = unsafe.objectFieldOffset(name);
        Teacher teacher = new Teacher();
        //使用cas方法修改成员变量的值，若期间不被别人先改则成功，被改了则失败
        unsafe.compareAndSwapInt(teacher, idOffset, 0, 20);
        unsafe.compareAndSwapObject(teacher, nameOffset, null, "张三");
        System.out.println(teacher);

        //自己使用Unsafe实现一个AtomicInteger
        //初始值一万，1000个线程同时执行-10操作，看结果是不是0
        MyAtomicInteger account = new MyAtomicInteger(10000);
        List<Thread> ts = new ArrayList<>();
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            ts.add(new Thread(() -> {
                account.withDraw(10);
            }));
        }
        ts.forEach(Thread::start);
        ts.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        long end = System.nanoTime();
        System.out.println(account.get() + " cost: " + (end - start) / 1000_000 + " ms");
    }
}

//自己使用Unsafe实现一个AtomicInteger
class MyAtomicInteger {
    private volatile int value;
    private static long VALUE_OFFSET;
    private static Unsafe UNSAFE;

    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            UNSAFE = (Unsafe)theUnsafe.get(null);

            VALUE_OFFSET = UNSAFE.objectFieldOffset(MyAtomicInteger.class.getDeclaredField("value"));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public MyAtomicInteger(int value) {
        this.value = value;
    }

    public int get() {
        return this.value;
    }

    public void withDraw(int num) {
        while (true) {
            int pre = this.value;
            int next = this.value - num;
            if (UNSAFE.compareAndSwapInt(this, VALUE_OFFSET, pre, next)) {
                break;
            }
        }
    }

}

class Teacher {
    private volatile int id;
    private volatile String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Teacher{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}