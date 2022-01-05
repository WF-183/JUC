package com.andy.nolock;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.atomic.LongAdder;

/**
 *
 * @author： <a href="mailto:wangfei@tianyancha.com">wangfei</a>
 * @date: 2022/1/4
 * @version: 1.0.0
 */
public class AtomicFieldUpdaterTest {

    public static void main(String[] args) {
        //AtomicIntegerFieldUpdater
        Student student = new Student();
        student.setAge(0);
        AtomicIntegerFieldUpdater<Student> atomicIntegerFieldUpdater = AtomicIntegerFieldUpdater.newUpdater(Student.class, "age");
        boolean b = atomicIntegerFieldUpdater.compareAndSet(student, 0, 20);

        AtomicReferenceFieldUpdater<Student, String> atomicReferenceFieldUpdater = AtomicReferenceFieldUpdater.newUpdater(Student.class, String.class, "name");
        boolean c = atomicReferenceFieldUpdater.compareAndSet(student, null, "andy");

    }
}

class Student {
    private volatile int age;
    private volatile String name;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Student{" + "age=" + age + ", name='" + name + '\'' + '}';
    }
}