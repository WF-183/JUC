package com.andy.lock;

import com.oracle.tools.packager.Log;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2021/12/23
 * @version: 1.0.0
 */
public class TicketTest {

    public static void main(String[] args) {
        //创建共享资源
        Ticket ticket = new Ticket();

        new Thread(() -> {
            //多线程里直接调用共享资源方法，加锁逻辑内部处理
            for (int i = 1; i <= 30; i++) {
                ticket.sale();
            }
        }, "AA").start();

        new Thread(() -> {
            //多线程里直接调用共享资源方法，加锁逻辑内部处理
            for (int i = 1; i <= 30; i++) {
                ticket.sale();
            }
        }, "BB").start();

    }
}

//资源类
@Slf4j
class Ticket {
    //共享资源
    private int number = 30;
    private Lock lock = new ReentrantLock();

    //所有同步资源操作方法写在资源类内部
    public void sale() {
        //lock锁
        //等价 用synchronized(this)锁住这块代码 == 用synchronized直接加在sale方法上
        lock.lock();
        try {
            //功能代码，按需写
            if (number > 0) {
                number--;
                log.info("此子线程卖出一张，总数还剩{}", number);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

}
