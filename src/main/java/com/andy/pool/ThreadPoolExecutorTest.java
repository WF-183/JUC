package com.andy.pool;

import com.andy.utils.JUCUtils;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2022/1/6
 * @version: 1.0.0
 */
@Slf4j
public class ThreadPoolExecutorTest {

    private static final String poolName = "AndyPool";

    public static final ThreadPoolExecutor executor =
        new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors() * 5, 2000, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(Runtime.getRuntime().availableProcessors() * 100), new ThreadFactoryBuilder().setNameFormat(poolName + "-thread-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public static void main(String[] args) throws Exception {


        test2();

    }

    private static void test1() {
        //创建一个个可被阻塞的异步任务，各任务点submit创建后瞬间就会开始执行
        List<Future<Object>> futures = new ArrayList<>();
        futures.add(executor.submit(() -> {
            log.info("1 begin");
            JUCUtils.sleep(1);
            return "1";
        }));
        futures.add(executor.submit(() -> {
            log.info("2 begin");
            JUCUtils.sleep(2);
            return "2";
        }));
        futures.add(executor.submit(() -> {
            log.info("3 begin");
            JUCUtils.sleep(3);
            return "3";
        }));
        //get阻塞获取结果
        //逐个get，全部阻塞，保证所有future都都执行完主流程再继续往下走
        futures.forEach(item -> {
            try {
                log.info("get {}", item.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });
        log.info("全都get到 主线程放行");

        //==》
        //23:52:51.343 [AndyPool-thread-0] INFO com.andy.pool.ThreadPoolExecutorTest - 1 begin
        //23:52:51.343 [AndyPool-thread-1] INFO com.andy.pool.ThreadPoolExecutorTest - 2 begin
        //23:52:51.343 [AndyPool-thread-2] INFO com.andy.pool.ThreadPoolExecutorTest - 3 begin
        //23:52:52.349 [main] INFO com.andy.pool.ThreadPoolExecutorTest - get 1
        //23:52:53.349 [main] INFO com.andy.pool.ThreadPoolExecutorTest - get 2
        //23:52:54.348 [main] INFO com.andy.pool.ThreadPoolExecutorTest - get 3
        //23:52:54.348 [main] INFO com.andy.pool.ThreadPoolExecutorTest - 全都get到 主线程放行
    }

    private static void test2() throws InterruptedException {
        //invokeAll
        //批量提交任务，返回值是一堆futures，需要靠futures遍历get阻塞获取结果
        //invokeAll(批量)和批量逐个submit提交效果不一样，invokeAll需要所有任务都完成才统一notify能从get拿到值，批量submit是每一个任务完成notify自己的get
        //但是宏观上效果是一样的，整体阻塞时间都取决于最耗时的一个子任务
        List<Future<Object>> futures = executor.invokeAll(Arrays.asList(() -> {
            log.info("1 begin");
            JUCUtils.sleep(1);
            return "1";
        }, () -> {
            log.info("2 begin");
            JUCUtils.sleep(2);
            return "2";
        }, () -> {
            log.info("3 begin");
            JUCUtils.sleep(3);
            return "3";
        }));

        futures.forEach(item -> {
            try {
                log.info("get {}", item.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        //==》
        //00:00:42.101 [AndyPool-thread-1] INFO com.andy.pool.ThreadPoolExecutorTest - 2 begin
        //00:00:42.101 [AndyPool-thread-0] INFO com.andy.pool.ThreadPoolExecutorTest - 1 begin
        //00:00:42.101 [AndyPool-thread-2] INFO com.andy.pool.ThreadPoolExecutorTest - 3 begin
        //00:00:45.110 [main] INFO com.andy.pool.ThreadPoolExecutorTest - get 1
        //00:00:45.112 [main] INFO com.andy.pool.ThreadPoolExecutorTest - get 2
        //00:00:45.112 [main] INFO com.andy.pool.ThreadPoolExecutorTest - get 3
    }

    private static void test3() throws InterruptedException, ExecutionException {
        //invokeAny
        //批量提交任务，任一任务先执行完则返回此任务结果作为最终结果，其它任务立刻全部舍弃取消
        //返回值不是future，不需要get
        Object o = executor.invokeAny(Arrays.asList(() -> {
            log.info("1 begin");
            JUCUtils.sleep(1);
            return "1";
        }, () -> {
            log.info("2 begin");
            JUCUtils.sleep(2);
            return "2";
        }, () -> {
            log.info("3 begin");
            JUCUtils.sleep(3);
            return "3";
        }));
        log.info("get {}", o);
    }

}
