package com.andy.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2021/12/24
 * @version: 1.0.0
 */
@Slf4j
public class AccountTest {
    public static void main(String[] args) throws InterruptedException {
        //两个账户，每人2000，来回随机金额转一千次，最后两个账户里加起来应该还是4000才对
        Account a = new Account(2000);
        Account b = new Account(2000);
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                a.transfer(b, randomAmount());
            }
        }, "t1");
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                b.transfer(a, randomAmount());
            }
        }, "t2");
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        // 查看转账2000次后的总金额
        log.debug("total:{}", (a.getMoney() + b.getMoney()));
    }

    //Random是线程安全的类
    static Random random = new Random();
    // 随机 1~100
    public static int randomAmount() {
        return random.nextInt(100) + 1;
    }
}


class Account {
    private int money;

    public Account(int money) {
        this.money = money;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void transfer(Account target, int amount) {
        //这里需要使用类锁，使用this是不行的，两个账户，涉及多个共享资源
        //此处不等价synchronized提到方法上，synchronized普通方法等于synchronized(this)锁住方法体
        //原理性用法，实际工作不可能这么做，锁住类，性能太差，无法接受
        synchronized (Account.class){
            if (this.money > amount) {
                this.setMoney(this.getMoney() - amount);
                target.setMoney(target.getMoney() + amount);
            }
        }
    }
}