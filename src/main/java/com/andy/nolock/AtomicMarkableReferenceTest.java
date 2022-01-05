package com.andy.nolock;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicMarkableReference;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2022/1/4
 * @version: 1.0.0
 */
@Slf4j
public class AtomicMarkableReferenceTest {
    public static void main(String[] args) throws InterruptedException {
        GarbageBag bag = new GarbageBag("满", true);
        AtomicMarkableReference<GarbageBag> ref = new AtomicMarkableReference<>(bag, bag.isFlag());

        //判断 垃圾袋是满的则换新的，不是则不换
        Thread.sleep(1000);
        GarbageBag prev = ref.getReference();
        //AtomicMarkableReference.compareAndSet(比较旧值，set新值，比较ref的mark是否等于指定值，本次操作后set ref的mark值)
        boolean success = ref.compareAndSet(prev, new GarbageBag("新垃圾袋", false), true, false);
        log.debug("换了么? {} ,ref.getReference()={} ,ref.isMarked()={} ", success, ref.getReference().toString(), ref.isMarked());

    }
}

class GarbageBag {
    String desc;
    boolean flag;

    public GarbageBag(String desc, boolean flag) {
        this.desc = desc;
        this.flag = flag;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    @Override
    public String toString() {
        return "GarbageBag{" + "desc='" + desc + '\'' + ", flag=" + flag + '}';
    }
}