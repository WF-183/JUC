package com.andy.communication;

import com.andy.utils.JUCUtils;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2021/12/23
 * @version: 1.0.0
 */
public class PingpangTest {

    public static void main(String[] args) {
        Pingpang pingpang = new Pingpang();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                JUCUtils.sleep(1);
                pingpang.inc();
            }
        }, "t1").start();

        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                JUCUtils.sleep(1);
                pingpang.dec();
            }
        }, "t2").start();

    }
}

@Slf4j
class Pingpang {

    private int num;

    public void inc() {
        synchronized (this) {
            //等待
            while (num != 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //干活
            num++;
            log.info("inc , num = {}", num);
            //唤醒
            this.notifyAll();
        }
    }

    public void dec() {
        synchronized (this) {
            //等待
            while (num == 0) {
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //干活
            num--;
            log.info("dec , num = {}", num);
            //唤醒
            this.notifyAll();
        }
    }

}
