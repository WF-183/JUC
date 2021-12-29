package com.andy.communication;

import com.andy.utils.JUCUtils;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2021/12/29
 * @version: 1.0.0
 */
@Slf4j
public class waitNotifyTest {

    final static Object obj = new Object();
    //烟
    static boolean hasCigarette = false;
    //外卖
    static boolean hasTakeout = false;

    public static void main(String[] args) {
        //api基本使用
        test1();

        //wait&notify配合使用
        //test2();
    }

    //api基本使用
    public static void test1() {
        new Thread(() -> {
            synchronized (obj) {
                log.debug("执行....");
                try {
                    obj.wait(); // 让线程在obj上一直等待下去
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("其它代码....");
            }
        }).start();

        new Thread(() -> {
            synchronized (obj) {
                log.debug("执行....");
                try {
                    obj.wait(); // 让线程在obj上一直等待下去
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("其它代码....");
            }
        }).start();

        JUCUtils.sleep(2);
        log.debug("唤醒 obj 上其它线程");
        synchronized (obj) {
            //唤醒obj一个线程
            //obj.notify();
            //唤醒obj所有等待线程
            obj.notifyAll();
            log.debug("notify后面代码 notify不阻塞，打点调用后继续往下跑");

            //notify唤醒，自身并不释放锁，
            //notify子线程内容执行完毕后才释放锁，其他被唤醒线程才能抢到锁执行，可不是notify一打点其他线程就立刻开始跑了
            JUCUtils.sleep(5);
            log.debug("notify后面代码 notify不释放锁，notify线程内容执行完才轮得到被唤醒线程执行");
        }
    }

    //wait&notify配合使用
    public static void test2() {
        new Thread(() -> {
            synchronized (obj) {
                log.debug("有烟没?[{}]", hasCigarette);
                while (!hasCigarette) {
                    log.debug("没烟，先歇会!");
                    try {
                        obj.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                //干活
                log.debug("干活 有烟没?[{}]", hasCigarette);
                if (hasCigarette) {
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没干成活...");
                }
            }
        }, "小南").start();

        new Thread(() -> {
            synchronized (obj) {
                log.debug("外卖送到没?[{}]", hasTakeout);
                while (!hasTakeout) {
                    log.debug("没外卖，先歇会!");
                    try {
                        obj.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                //干活
                log.debug("干活 外卖送到没?[{}]", hasTakeout);
                if (hasTakeout) {
                    log.debug("可以开始干活了");
                } else {
                    log.debug("没干成活...");
                }
            }
        }, "小女").start();

        //wait&notify比sleep好处是不阻塞其他人
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                synchronized (obj) {
                    log.debug("可以开始干活了");
                }
            }, "其它人").start();
        }

        JUCUtils.sleep(2);
        new Thread(() -> {
            synchronized (obj) {
                hasTakeout = true;
                log.debug("外卖到了噢!");
                obj.notifyAll();
            }
        }, "送外卖的").start();

        JUCUtils.sleep(2);
        new Thread(() -> {
            synchronized (obj) {
                hasCigarette = true;
                log.debug("烟到了噢!");
                obj.notifyAll();
            }
        }, "送烟的").start();
    }

}



