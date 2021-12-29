package com.andy.communication;

import com.andy.utils.JUCUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2021/12/29
 * @version: 1.0.0
 */
@Slf4j
public class ProducerConsumerTest {//并发编程模式 - 生产者消费者模式

    public static void main(String[] args) {

        //capacity=2模拟队列满，capacity=20000正常情况
        MessageQueue messageQueue = new MessageQueue(2);

        //3个producer
        for (int i = 0; i < 3; i++) {
            int temp = i;
            new Thread(() -> {
                messageQueue.put(new Message(temp, "内容" + temp));
            }, "producer" + i).start();
        }

        //2个consumer
        for (int i = 0; i < 2; i++) {
            new Thread(() -> {
                //每个消费者都不停的尝试消费任务
                while (true) {
                    JUCUtils.sleep(1);
                    //理解 prodcer中创建的msg对象被通过messageQueue传递到了consumer
                    Message message = messageQueue.get();
                    //获取一条消息 下面进行各种自定义业务处理
                }
            }, "consumer" + i).start();
        }

    }

}

//消息队列类
@Slf4j
class MessageQueue {

    //容器
    private LinkedList<Message> list = new LinkedList<>();
    //容量
    private int capacity;

    public MessageQueue(int capacity) {
        this.capacity = capacity;
    }

    public void put(Message msg) {
        synchronized (list) {
            //队列满，等待
            while (capacity == list.size()) {
                try {
                    log.info("队列满，等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //干活 约定队尾放
            list.addLast(msg);
            log.info("put一条消息，msg={}", msg);
            //唤醒get中阻塞
            list.notifyAll();
            return;
        }
    }

    public Message get() {
        synchronized (list) {
            //队列空，等待
            while (list.size() == 0) {
                try {
                    log.info("队列空，等待");
                    list.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //干活 约定队首取
            Message msg = list.removeFirst();
            log.info("get一条消息，msg={}", msg);
            //唤醒put中阻塞
            list.notifyAll();
            return msg;
        }
    }

}

//消息类
//Message类只提供构造器和get方法，刻意不加set方法，只让读不让改它就绝对成了线程安全的。
final class Message {

    private int id;
    private Object value;

    public Message(int id, Object value) {
        this.id = id;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Message{" + "id=" + id + ", value=" + value + '}';
    }
}
