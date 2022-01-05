package com.andy.immutable;

import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2022/1/5
 * @version: 1.0.0
 */
@Slf4j
public class DateTimeFormatterTest {

    public static void main(String[] args) {

        //SimpleDateFormat不是线程安全的，多线程同时调用一个sdf对象的parse方法，会报错NumberFormatException
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    log.debug("{}", sdf.parse("1951-04-21"));
                } catch (Exception e) {
                    log.error("{}", e);
                }
            }).start();
        }


        //SimpleDateFormat不是线程安全的，多线程同时调用一个sdf对象的parse方法，会报错NumberFormatException
        //DateTimeFormatter是线程安全的不可变的 This class is immutable and thread-safe.
        //1、创建DateTimeFormatter对象
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        //2、日期对象=》字符串
        LocalDateTime ld = LocalDateTime.now();
        String str = format.format(ld);
        System.out.println(str);

        //3、字符串=》日期对象
        LocalDateTime ld2 = LocalDateTime.parse("2010-01-01 10:12:40", format);
        System.out.println(ld2);

    }
}
