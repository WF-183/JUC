package com.andy.jmm;

import com.andy.utils.JUCUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2022/1/1
 * @version: 1.0.0
 */
@Slf4j
public class ValatileTest {

    static boolean run = true;

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            while (run) {
            }
        }, "t1").start();

        // 线程t不会如预想的停下来
        JUCUtils.sleep(2);
        log.info("false");
        run = false;
    }

}

//valatile会顺带把上面变量修改也刷到主存
class ShunDaiTest {

    static int y;
    volatile static int x;

    public static void main(String[] args) {

        new Thread(() -> {
            y = 10;
            x = 20;
            //写屏障把x写回主存时会把y也写回去
        }, "t1").start();

        new Thread(() -> {
            // x=20 对 t2 可见, 同时 y=10 也对 t2 可见
            System.out.println(x);
            System.out.println(y);
        }, "t2").start();
    }

}
