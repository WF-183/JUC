package com.andy.future;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.*;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2021/11/28
 * @version: 1.0.0
 */
public class CompletableFutureTest {




    public static void main(String[] args) throws ExecutionException, InterruptedException {

        test5();
    }


    public static void test5() throws ExecutionException, InterruptedException {
        //CompletableFuture异步方法都可以指定一个线程池作为任务的执行者，不传此参数就会使用默认ForkJoinPool线程池来执行。
        //实际使用请全都显式指定ThreadPoolExecutor线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 40,
            2000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(2000));

        //executor.submit()
        //executor.execute();

        //输出：
        //2 submit 不定，点submit后就会立即触发子线程执行，实测如此
        //get阻塞获取结果，然后主流程继续往下走，三秒后才输出3
        Future<Integer> future = executor.submit(() -> {
            System.out.println("submit");
            Thread.sleep(3000L);
            return 1;
        });
        System.out.println(2);
        //阻塞等待
        Integer integer = future.get();
        System.out.println(3);


    }


    public static void test4(){
        //CompletableFuture异步方法都可以指定一个线程池作为任务的执行者，不传此参数就会使用默认ForkJoinPool线程池来执行。
        //实际使用请全都显式指定ThreadPoolExecutor线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 40,
            2000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(2000));



        //复杂异步流程控制场景
        //同时从新浪和网易查询指定证券代码，只要任意一个返回结果，就进行下一步查询价格，
        //查询价格也同时从新浪和网易查询，只要任意一个返回结果，就完成操作

        //输出：
        //从新浪查询指定证券代码
        //从新浪查询指定证券代码对应价格
        //price1

        //// 两个CompletableFuture执行异步查询
        //CompletableFuture<String> codeQueryFromSinaFuture = CompletableFuture.supplyAsync(() -> {
        //    Thread.sleep(1000L);
        //    System.out.println("从新浪查询指定证券代码");
        //    return "code1";
        //});
        //CompletableFuture<String> codeQueryFrom163Future = CompletableFuture.supplyAsync(() -> {
        //    Thread.sleep(3000L);
        //    System.out.println("从网易查询指定证券代码");
        //    return "code2";
        //});
        //// 用anyOf合并为一个新的CompletableFuture
        //CompletableFuture<Object> codeAnyOfFuture = CompletableFuture.anyOf(codeQueryFromSinaFuture, codeQueryFrom163Future);
        //
        //
        //// 两个CompletableFuture执行异步查询
        //CompletableFuture<String> priceQueryFromSinaFuture = codeAnyOfFuture.thenApplyAsync((code) -> {
        //    Thread.sleep(1000L);
        //    System.out.println("从新浪查询指定证券代码对应价格");
        //    return "price1";
        //});
        //CompletableFuture<String> priceQueryFrom163Future = codeAnyOfFuture.thenApplyAsync((code) -> {
        //    Thread.sleep(3000L);
        //    System.out.println("从网易查询指定证券代码对应价格");
        //    return "price2";
        //});
        //// 用anyOf合并为一个新的CompletableFuture
        //CompletableFuture<Object> priceAnyOfFuture = CompletableFuture.anyOf(priceQueryFromSinaFuture, priceQueryFrom163Future);
        //
        ////获取结果
        //try {
        //    Object resPrice = priceAnyOfFuture.get();
        //    System.out.println((String) resPrice);
        //} catch (Exception e) {
        //    e.printStackTrace();
        //}
        //


    }

    public static void test3(){
        //CompletableFuture异步方法都可以指定一个线程池作为任务的执行者，不传此参数就会使用默认ForkJoinPool线程池来执行。
        //实际使用请全都显式指定ThreadPoolExecutor线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 40,
            2000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(2000));

        //输出 I SAY HELLO WORLD 无输入无输出
        String str = "i say ";
        //CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> { return str; }, executor)
        //    .thenApply(item -> item + "hello world")
        //    .thenApply(item -> item.toUpperCase(Locale.ROOT))
        //    .thenAccept(item -> System.out.println(item))
        //    .thenRun(()-> System.out.println("无输入无输出"));

        //输出 I SAY HELLO WORLD combine
        //CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> { return str; }, executor)
        //    .thenApply(item -> item + "hello world")
        //    .thenApply(item -> item.toUpperCase(Locale.ROOT))
        //    .thenCombine(CompletableFuture.completedFuture(" combine"),(s1,s2)->s1+s2)
        //    .thenAccept(item -> System.out.println(item));

        //输出 当前子线程任务执行失败，taskId: 1 ,异常信息如下java.lang.ArithmeticException: / by zero
        Integer taskId = 1;
        CompletableFuture.supplyAsync(() -> {
            int i = 10/0;
            return taskId;
        }, executor).exceptionally((ex) -> {
            System.out.println("当前子线程任务执行失败，taskId: "+taskId+" ,异常信息如下");
            ex.printStackTrace();
            return null;
        });



    }

    public static void test2(){
        //CompletableFuture异步方法都可以指定一个线程池作为任务的执行者，不传此参数就会使用默认ForkJoinPool线程池来执行。
        //实际使用请全都显式指定ThreadPoolExecutor线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 40,
            2000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(2000));

        List<CompletableFuture> futures = Lists.newArrayList();
        List<String> lowerList = Lists.newArrayList("a", "b", "c");
        for (String s : lowerList) {
            CompletableFuture<String> future1 = CompletableFuture.supplyAsync(() -> {return s;}, executor).thenApply(item->item.toUpperCase(Locale.ROOT));
            futures.add(future1);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();

        for (CompletableFuture future : futures) {
            try {
                System.out.println(future.get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }


    }




    public static void test1()  throws ExecutionException, InterruptedException {

        //CompletableFuture异步方法都可以指定一个线程池作为任务的执行者，不传此参数就会使用默认ForkJoinPool线程池来执行。
        //实际使用请全都显式指定ThreadPoolExecutor线程池
        ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 40,
            2000, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(2000));


        //效果：
        // 123 间不定 ，CompletableFuture.runAsync后其内代码将立刻触发执行，实测如此，
        //一秒后1 end，三秒后2 end，
        //最后输出4，allOf 所有future并行任务阻塞收集完之前，4绝不会被执行，要得就是这个效果
        List<Future> futures = new ArrayList<>();
        CompletableFuture<Void> future1 = CompletableFuture.runAsync(() -> {
            //任意多线程代码
            System.out.println("1");
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("1 end");
        }, executor);
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            //任意多线程代码
            System.out.println("2");
            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("2 end");
        }, executor);
        futures.add(future1);
        futures.add(future2);


        System.out.println("3");
        try {
            //join和get都有阻塞收集的效果，实测如此
            //allOf 所有future结果阻塞收集完，才放行继续执行主流程代码，实测如此，绝大部分情况只要这个效果
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("4");



    }



}
