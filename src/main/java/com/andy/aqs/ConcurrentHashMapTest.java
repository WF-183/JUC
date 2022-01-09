package com.andy.aqs;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2022/1/9
 * @version: 1.0.0
 */
public class ConcurrentHashMapTest {

    public static void main(String[] args) {

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();
        Thread thread = new Thread(() -> {
            //错误用法 线程安全类的每个方法是原子的安全的，但是多个方法组合起来用就不是线程安全的了，
            //注意就算此时已经换了实现类为ConcurrentHashMap，这样用也是错的
            Integer counter = map.get(word);
            int newValue = counter == null ? 1 : counter + 1;
            map.put(word, newValue);
        });

        ConcurrentHashMap<String, LongAdder> map = new ConcurrentHashMap<>();
        Thread thread = new Thread(() -> {
            //正确用法 等价正确写法
            //computeIfAbsent()方法 若不存在则生成一个v 把k-v放入，若存在则相当于get(key)
            LongAdder value = map.computeIfAbsent(word, (key) -> new LongAdder());
            value.increment();
        });

    }

    //初始化哈希表 懒加载
    //while+if cas
    private final Node<K, V>[] initTable() {
        Node<K, V>[] tab;
        int sc;
        while ((tab = table) == null || tab.length == 0) {
            if ((sc = sizeCtl) < 0)
                Thread.yield();
                // 尝试将 sizeCtl 设置为 -1(表示初始化 table)
            else if (U.compareAndSwapInt(this, SIZECTL, sc, -1)) {
                // 获得锁, 创建 table, 这时其它线程会在 while() 循环中 yield 直至 table 创建
                try {
                    if ((tab = table) == null || tab.length == 0) {
                        int n = (sc > 0) ? sc : DEFAULT_CAPACITY;
                        Node<K, V>[] nt = (Node<K, V>[])new Node<?, ?>[n];
                        table = tab = nt;
                        sc = n - (n >>> 2);
                    }
                } finally {
                    sizeCtl = sc;
                }
                break;
            }
        }
        return tab;
    }

    public ConcurrentHashMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        if (!(loadFactor > 0.0f) || initialCapacity < 0 || concurrencyLevel <= 0)
            throw new IllegalArgumentException();
        if (initialCapacity < concurrencyLevel) // Use at least as many bins
            initialCapacity = concurrencyLevel; // as estimated threads
        long size = (long)(1.0 + (long)initialCapacity / loadFactor);
        // tableSizeFor 仍然是保证计算的大小是 2^n, 即 16,32,64 ...
        int cap = (size >= (long)MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : tableSizeFor((int)size);
        this.sizeCtl = cap;
    }

    public V get(Object key) {
        Node<K, V>[] tab;
        Node<K, V> e, p;
        int n, eh;
        K ek;
        // spread 方法能确保返回结果是正数
        int h = spread(key.hashCode());
        if ((tab = table) != null && (n = tab.length) > 0 && (e = tabAt(tab, (n - 1) & h)) != null) { // 如果头结点已经是要查找的 key
            if ((eh = e.hash) == h) {
                if ((ek = e.key) == key || (ek != null && key.equals(ek)))
                    return e.val;
            }
            // hash 为负数表示该 bin 在扩容中或是 treebin, 这时调用 find 方法来查找
            else if (eh < 0)
                return (p = e.find(h, key)) != null ? p.val : null; // 正常遍历链表, 用 equals 比较
            while ((e = e.next) != null) {
                if (e.hash == h && ((ek = e.key) == key || (ek != null && key.equals(ek))))
                    return e.val;
            }
        }
        return null;
    }

    public V put(K key, V value) {
        return putVal(key, value, false);
    }

    final V putVal(K key, V value, boolean onlyIfAbsent) {
        if (key == null || value == null)
            throw new NullPointerException(); // 其中 spread 方法会综合高位低位, 具有更好的 hash 性
        int hash = spread(key.hashCode());
        int binCount = 0;
        for (Node<K, V>[] tab = table; ; ) {
            // f 是链表头节点
            // fh 是链表头结点的 hash
            // i 是链表在 table 中的下标
            Node<K, V> f;
            int n, i, fh;
            // 要创建 table
            if (tab == null || (n = tab.length) == 0)
                // 初始化 table 使用了 cas, 无需 synchronized 创建成功, 进入下一轮循环
                tab = initTable();
                // 要创建链表头节点
            else if ((f = tabAt(tab, i = (n - 1) & hash)) == null) {
                // 添加链表头使用了 cas, 无需 synchronized
                if (casTabAt(tab, i, null, new Node<K, V>(hash, key, value, null)))
                    break;
            }
            // 帮忙扩容
            else if ((fh = f.hash) == MOVED)
                // 帮忙之后, 进入下一轮循环
                tab = helpTransfer(tab, f);
            else {
                V oldVal = null; // 锁住链表头节点
                synchronized (f) {
                    // 再次确认链表头节点没有被移动
                    if (tabAt(tab, i) == f) {
                        // 链表
                        if (fh >= 0) {
                            binCount = 1;
                            // 遍历链表
                            for (Node<K, V> e = f; ; ++binCount) {
                                K ek;
                                // 找到相同的 key
                                if (e.hash == hash && ((ek = e.key) == key || (ek != null && key.equals(ek)))) {
                                    oldVal = e.val;
                                    // 更新
                                    if (!onlyIfAbsent)
                                        e.val = value;
                                    break;
                                }
                                Node<K, V> pred = e;
                                // 已经是最后的节点了, 新增 Node, 追加至链表尾
                                if ((e = e.next) == null) {
                                    pred.next = new Node<K, V>(hash, key, value, null);
                                    break;
                                }
                            }
                        }
                        // 红黑树
                        else if (f instanceof TreeBin) {
                            Node<K, V> p;
                            binCount = 2;
                            // putTreeVal 会看 key 是否已经在树中, 是, 则返回对应的 TreeNode
                            if ( (p = ((TreeBin<K, V>)f).putTreeVal(hash, key, oldVal = p.val; if (!onlyIfAbsent)
                                p.val = value;
                        }
                    }
                }
                // 释放链表头节点的锁 }
                if (binCount != 0) {
                    if (binCount >= TREEIFY_THRESHOLD)
                        // 如果链表长度 >= 树化阈值(8), 进行链表转为红黑树
                        treeifyBin(tab, i);
                    if (oldVal != null)
                        return oldVal;
                    break;
                }
            }
        }
        // 增加 size 计数
        addCount(1L, binCount);
        return null;
    }

}
