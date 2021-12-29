package com.andy.communication;

import com.andy.utils.JUCUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2021/12/29
 * @version: 1.0.0
 */
@Slf4j
public class GuardedObjectTest {//并发编程模式- 保护性暂停模式

    public static void main(String[] args) {
        ////核心架子
        //GuardedObject guardedObject = new GuardedObject();
        //new Thread(()->{
        //    log.info("线程1做一些事");
        //
        //    //阻塞等在这一行
        //    Object response = guardedObject.get(3000);
        //    log.info("线程1阻塞等待线程2部分代码执行完，拿到其传递的值 或 超出时限不等了往下走(增强效果)" + response);
        //
        //    log.info("线程1再做一些事");
        //},"t1").start();
        //
        //
        //new Thread(()->{
        //    log.info("线程2做一些事");
        //    JUCUtils.sleep(1);
        //    log.info("线程2做完");
        //    guardedObject.setComplete(new Object());
        //
        //    //与join相比好处时，不要求整个线程内容执行完才能传递结果，set后依然可以再做一些自己的事
        //    log.info("线程2再做一些事");
        //},"t2").start();


        //母题-送信
        //创建信箱
        Boxes boxes = new Boxes();

        //3居民
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                GuardedObject guardedObject = boxes.createGuardedObject();
                log.info("开始收信 格子ID={}", guardedObject.getId());
                Object mailContent = guardedObject.get(3000);
                log.info("收到信 格子ID={}，信内容={}", guardedObject.getId(), mailContent);
            }, "居民" + i).start();
        }

        JUCUtils.sleep(2);
        //3送信员
        for (Map.Entry<Integer, GuardedObject> entry : boxes.getMap().entrySet()) {
            Integer objId = entry.getKey();
            GuardedObject guardedObject = entry.getValue();
            new Thread(() -> {
                log.info("开始送信 格子ID={}", guardedObject.getId());
                guardedObject.setComplete("信件内容" + objId);
                log.info("送信完成 格子ID={}", guardedObject.getId());
            }, "送信员" + objId).start();
        }



    }

}

//邮箱类
class Boxes {

    private Map<Integer, GuardedObject> map = new ConcurrentHashMap<>();
    private int objId;

    private int generateId() {
        synchronized (this) {
            objId++;
        }
        return objId;
    }

    /**
     * 创建邮箱格子
     * @return
     */
    public GuardedObject createGuardedObject() {
        GuardedObject guardedObject = new GuardedObject(generateId());
        map.put(guardedObject.getId(), guardedObject);
        return guardedObject;
    }

    /**
     * 获取所有格子
     * @return
     */
    public Map<Integer, GuardedObject> getMap() {
        return map;
    }
}

//结果传递类
class GuardedObject {

    private int id;
    private Object response;

    public GuardedObject(int id) {
        this.id = id;
    }

    public GuardedObject() {
    }

    public int getId() {
        return id;
    }

    /**
     * get 阻塞等待，获取另一线程部分代码执行结果
     * @param waitTimeMs 超时时间，最多等多久
     * @return
     */
    public Object get(long waitTimeMs) {
        synchronized (this) {
            long start = System.currentTimeMillis();
            long passedTime = 0;
            while (response == null) {
                try {
                    this.wait(waitTimeMs - passedTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                passedTime = System.currentTimeMillis() - start;
                if (passedTime > waitTimeMs) {
                    break;
                }
            }
        }
        return response;
    }

    /**
     * set 写入另一线程部分代码执行结果，唤醒等待线程
     * @param obj
     */
    public void setComplete(Object obj) {
        synchronized (this) {
            this.response = obj;
            this.notifyAll();
        }
    }

}