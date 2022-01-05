package com.andy.nolock;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2022/1/4
 * @version: 1.0.0
 */
public class AtomicReferenceTest {

    public static void main(String[] args) {
        //初始值一万，1000个线程同时执行-10操作，看结果是不是0
        AccountCas account = new AccountCas(10000);
        List<Thread> ts = new ArrayList<>();
        long start = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            ts.add(new Thread(() -> {
                account.withdraw(10);
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
        System.out.println(account.getBalance() + " cost: " + (end - start) / 1000_000 + " ms");
    }
}

class AccountReference {
    //要保护的共享资源是引用类型BigDecimal
    private AtomicReference<BigDecimal> atomicRefBalance;

    public AccountReference(Integer atomicRefBalance) {
        //这里要new
        this.atomicRefBalance = new AtomicReference(atomicRefBalance);
    }

    public BigDecimal getAtomicRefBalance() {
        return atomicRefBalance.get();
    }

    public void withdraw(BigDecimal amount) {
        //cas核心原理操作
        while (true) {
            BigDecimal prev = atomicRefBalance.get();
            BigDecimal next = prev.subtract(amount);
            if (atomicRefBalance.compareAndSet(prev, next)) {
                break;
            }
        }
    }
}
