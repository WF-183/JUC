package com.andy.thread;

import com.andy.utils.JUCUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.LockSupport;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2021/12/30
 * @version: 1.0.0
 */
@Slf4j
public class ParkTest {

    public static void main(String[] args) {

        Thread t1 = new Thread(() -> {

            log.debug("park...");
            LockSupport.park();  //会卡在这一行

            log.debug("interrupt 1s后打断，继续往下执行，打断状态:{}", Thread.currentThread().isInterrupted()); //true

            LockSupport.park();  //打断标志位此时为true，park不生效，线程将继续往下跑，不会卡在这
            log.debug("没park住 打断标志位此时为true");

            Thread.interrupted(); //此方法获取打断标志位后，将标志位设为false
            LockSupport.park();  //打断标志位此时为false，park生效，线程卡在这

            log.debug("3s后 t1 unpark，继续往下执行");

            JUCUtils.sleep(4);
            LockSupport.park();  //打断标志位此时为false，park生效，线程卡在这
            log.debug("没park住 提前unpark解毒");

        }, "t1");
        t1.start();

        JUCUtils.sleep(1);
        t1.interrupt();  //1s后 t1.interrupt(); 打断，继续往下执行

        JUCUtils.sleep(3);
        LockSupport.unpark(t1); //3s后 t1 unpark，继续往下执行

        JUCUtils.sleep(2);
        LockSupport.unpark(t1); //2s后 提前unpark解毒，下一次park将失效

    }

}
