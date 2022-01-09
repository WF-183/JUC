package com.andy.lock;

import com.andy.utils.JUCUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2022/1/9
 * @version: 1.0.0
 */
public class ReentrantReadWriteLockTest {//读写锁

    public static void main(String[] args) {
        DataContainer dataContainer = new DataContainer();

        //读读不互斥
        new Thread(() -> {
            dataContainer.read();
        }, "t1").start();
        new Thread(() -> {
            dataContainer.read();
        }, "t2").start();
        //==>
        //12:13:44.145 [t2] DEBUG com.andy.lock.DataContainer - 获取到读锁 读取
        //12:13:44.145 [t1] DEBUG com.andy.lock.DataContainer - 获取到读锁 读取
        //12:13:45.158 [t2] DEBUG com.andy.lock.DataContainer - 释放读锁...
        //12:13:45.158 [t1] DEBUG com.andy.lock.DataContainer - 释放读锁...

        //读写互斥，写写互斥同理
        new Thread(() -> {
            dataContainer.read();
        }, "t1").start();
        new Thread(() -> {
            dataContainer.write();
        }, "t2").start();
        //==>
        //12:15:32.977 [t1] DEBUG com.andy.lock.DataContainer - 获取到读锁 读取
        //12:15:33.987 [t1] DEBUG com.andy.lock.DataContainer - 释放读锁...
        //12:15:33.988 [t2] DEBUG com.andy.lock.DataContainer - 获取到写锁 写入
        //12:15:34.991 [t2] DEBUG com.andy.lock.DataContainer - 释放写锁...
    }

}

//基本使用
@Slf4j
class DataContainer {
    private Object data;
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
    private ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();

    public Object read() {
        readLock.lock();
        try {
            log.debug("获取到读锁 读取");
            JUCUtils.sleep(1);
            return data;
        } finally {
            log.debug("释放读锁...");
            readLock.unlock();
        }
    }

    public void write() {
        writeLock.lock();
        try {
            log.debug("获取到写锁 写入");
            JUCUtils.sleep(1);
        } finally {
            log.debug("释放写锁...");
            writeLock.unlock();
        }
    }
}
//
//class GenericCachedDao<T> {
//    //HashMap不是线程安全的
//    HashMap<SqlPair, T> map = new HashMap<>();
//    ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
//    GenericDao genericDao = new GenericDao();
//
//    public int update(String sql, Object... params) {
//        SqlPair key = new SqlPair(sql, params);
//        // 加写锁, 防止其它线程对缓存读取和更改
//        lock.writeLock().lock();
//        try {
//            int rows = genericDao.update(sql, params);
//            map.clear();
//            return rows;
//        } finally {
//            lock.writeLock().unlock();
//        }
//    }
//
//    public T queryOne(Class<T> beanClass, String sql, Object... params) {
//        SqlPair key = new SqlPair(sql, params);
//        // 加读锁, 读读不互斥 没人改时全放行，读写互斥 其它线程对缓存更改时阻塞住
//        lock.readLock().lock();
//        try {
//            T value = map.get(key);
//            if (value != null) {
//                return value;
//            }
//        } finally {
//            lock.readLock().unlock();
//        }
//
//        // 加写锁, 防止其它线程对缓存读取和更改
//        lock.writeLock().lock();
//        try {
//            //双重检查
//            T value = map.get(key);
//            if (value != null) {
//                return value;
//            }
//            value = genericDao.queryOne(beanClass, sql, params);
//            map.put(key, value);
//            return value;
//        } finally {
//            lock.writeLock().unlock();
//        }
//    }
//
//}
//
//
//
//class SqlPair {
//    private String sql;
//    private Object[] params;
//
//    public SqlPair(String sql, Object[] params) {
//        this.sql = sql;
//        this.params = params;
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//        if (o == null || getClass() != o.getClass()) {
//            return false;
//        }
//        SqlPair sqlPair = (SqlPair)o;
//        return sql.equals(sqlPair.sql) && Arrays.equals(params, sqlPair.params);
//    }
//
//    @Override
//    public int hashCode() {
//        int result = Objects.hash(sql);
//        result = 31 * result + Arrays.hashCode(params);
//        return result;
//    }
//}