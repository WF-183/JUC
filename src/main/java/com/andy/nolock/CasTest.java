package com.andy.nolock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2022/1/4
 * @version: 1.0.0
 */
public class CasTest {

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

class AccountCas {
    //使用AtomicInteger代替Integer
    private AtomicInteger balance;

    public AccountCas(Integer balance) {
        //这里要new
        this.balance = new AtomicInteger(balance);
    }

    public Integer getBalance() {
        return balance.get();
    }

    public void withdraw(Integer amount) {
        //cas核心原理操作
        while (true) {
            int prev = balance.get();
            int next = prev - amount;
            if (balance.compareAndSet(prev, next)) {
                break;
            }
        }
        //等价简化写法
        // balance.addAndGet(-amount);
    }
}
