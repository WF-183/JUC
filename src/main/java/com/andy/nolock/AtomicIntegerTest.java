package com.andy.nolock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2022/1/4
 * @version: 1.0.0
 */
public class AtomicIntegerTest {

    public static void main(String[] args) {
        //原子整数
        //AtomicInteger、 AtomicLong 、AtomicBoolean
        AtomicInteger atomicInteger = new AtomicInteger(1);
        //其单个方法都能保证内部是原子操作
        //步长1加减
        atomicInteger.getAndIncrement();// i++
        atomicInteger.incrementAndGet();// ++i
        atomicInteger.getAndDecrement();// i--
        atomicInteger.decrementAndGet();// --i
        //步长n变动加减
        atomicInteger.getAndAdd(5);// 先get，再加n
        atomicInteger.addAndGet(5);// 先加n，再get
        //任意加减乘除运算
        atomicInteger.getAndUpdate(item -> item * 5);// 先get，再运算
        atomicInteger.updateAndGet(item -> item * 5);// 先运算，再get

        //任意加减乘除运算
        // 获取并计算 (p 为 atomicInteger 的当前值, x 为参数1)
        System.out.println(atomicInteger.getAndAccumulate(10, (item, x) -> item + x));
        // 计算并获取 (p 为 atomicInteger 的当前值, x 为参数1)
        System.out.println(atomicInteger.accumulateAndGet(-10, (item, x) -> item + x));

    }



}
