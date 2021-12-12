package com.andy.future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutionException;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2021/12/12
 * @version: 1.0.0
 */
public class Log4jBugTest {

    /**
     * log4j大bug
     * 问题详情：
     * https://open.work.weixin.qq.com/wwopen/mpnews?mixuin=3_HVCQAABwBHKvwGAAAUAA&mfid=WW0310-J2gDqAAABwC3w4RT9pQ2Mgn3Bdl5c&idx=0&sn=e38a1a3cbc49335ea028b9cbfa0d419a&version=3.1.20.90367&platform=mac
     */

    private static final Logger logger = LogManager.getLogger();


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //log4j依赖引入版本 2.14.0 ，确实有问题
        //14:43:32.421 [main] INFO  com.andy.future.Log4jBugTest - os = Mac OS X 10.14.6 unknown, architecture: x86_64-64
        String username1 = "${java:os}";
        logger.info("os = {}",username1);
        //14:43:32.423 [main] INFO  com.andy.future.Log4jBugTest - vm = Java HotSpot(TM) 64-Bit Server VM (build 25.181-b13, mixed mode)
        String username2 = "${java:vm}";
        logger.info("vm = {}",username2);


        //log4j依赖引入版本 2.15.0 ，确实修复了
        //14:46:54.634 [main] INFO  com.andy.future.Log4jBugTest - os = ${java:os}
        String username3 = "${java:os}";
        logger.info("os = {}",username3);
        //14:46:54.637 [main] INFO  com.andy.future.Log4jBugTest - vm = ${java:vm}
        String username4 = "${java:vm}";
        logger.info("vm = {}",username4);



    }



}
