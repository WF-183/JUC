package com.andy.dispatch;

import com.andy.utils.JUCUtils;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.*;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2022/1/9
 * @version: 1.0.0
 */
@Slf4j
public class CyclicBarrierTest {



    public static void main(String[] args) {

        //个数为2时才会继续执行，到了后会自动回复初始值，可再次使用
        CyclicBarrier cb = new CyclicBarrier(2, () -> {
            log.info("到达一次循环屏障");
        });
        ExecutorService executor = Executors.newFixedThreadPool(2);

        for (int i = 0; i < 3; i++) {
            executor.submit(() -> {
                log.info("线程1开始..");
                JUCUtils.sleep(1);
                try {
                    cb.await(); // 当个数不足时，等待
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                log.info("线程1继续向下运行..");
            });
            executor.submit(() -> {
                log.info("线程2开始..");
                JUCUtils.sleep(2);
                try {
                    cb.await(); // 当个数不足时，等待
                } catch (InterruptedException | BrokenBarrierException e) {
                    e.printStackTrace();
                }
                log.info("线程2继续向下运行..");
            });
        }

    }
}
