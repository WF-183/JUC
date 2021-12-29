package com.andy.utils;

import java.util.concurrent.TimeUnit;

/**
 * 通用的简化方法
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2021/12/22
 * @version: 1.0.0
 */
public class JUCUtils {


    public static void sleepSecs(int secs){
        try {
            TimeUnit.SECONDS.sleep(secs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sleep(int secs){
        try {
            TimeUnit.SECONDS.sleep(secs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




}
