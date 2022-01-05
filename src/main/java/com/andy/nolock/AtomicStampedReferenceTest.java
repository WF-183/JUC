package com.andy.nolock;

import com.andy.utils.JUCUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicStampedReference;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2022/1/4
 * @version: 1.0.0
 */
@Slf4j
public class AtomicStampedReferenceTest {

    //new时指定值和初始版本号
    static AtomicStampedReference<String> ref = new AtomicStampedReference<>("A", 0);

    public static void main(String[] args) throws InterruptedException {
        log.debug("main start...");
        //获取值 A
        String prev = ref.getReference();
        //获取版本号
        int stamp = ref.getStamp();

        //模拟期间被改
        other();
        JUCUtils.sleep(1);

        //如果中间有其它线程干扰，发生了 ABA 现象，尝试改为C将失败
        log.debug("change A->C {}", ref.compareAndSet(prev, "C", stamp, stamp + 1));
    }

    private static void other() {
        new Thread(() -> {
            //AtomicStampedReference.compareAndSet(比较旧值，set新值，比较版本号，本次操作后set版本号);
            log.debug("change A->B {}", ref.compareAndSet(ref.getReference(), "B", ref.getStamp(), ref.getStamp() + 1));
            log.debug("更新版本为1 {}", ref.getStamp());
        }, "t1").start();
        JUCUtils.sleep(0.5);
        new Thread(() -> {
            log.debug("change B->A {}", ref.compareAndSet(ref.getReference(), "A", ref.getStamp(), ref.getStamp() + 1));
            log.debug("更新版本为2 {}", ref.getStamp());
        }, "t2").start();
    }
}
