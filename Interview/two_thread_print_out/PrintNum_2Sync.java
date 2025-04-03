package com.tyron;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 这是最基础的线程同步方案，通过共享锁对象和内置的等待/通知机制实现交替执行。
 * 实现步骤：
 * 定义一个共享计数器 count 和锁对象（如 Object lock）。
 * 线程A时打印并唤醒线程B；线程B打印并唤醒线程A。
 * 每次打印后递增 count，直到达到200。
 *
 * 使用 synchronized 确保线程安全。
 * wait() 释放锁并进入等待状态，notify() 唤醒等待线程。
 * 循环中必须用 while 而非 if，防止虚假唤醒。
 */
public class PrintNum_2Sync {

    /**
     * 静态原子整型变量，用于在多线程环境下进行原子操作。
     * 该变量初始值为0，通过AtomicInteger类提供的原子方法，可以确保在多线程环境下的线程安全操作。
     */
    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    /*
     * 用于线程同步的静态锁对象。
     */
    private static Object object = new Object();

    public static void main(String[] args) {
        // 创建一个线程，执行一个Lambda表达式
        new Thread(() -> {
            // 当atomicInteger的值小于等于200时，执行循环
            while (atomicInteger.get() <= 200) {
                // 使用synchronized关键字，保证线程安全
                synchronized (object) {
                    // 唤醒其他等待的线程
                    object.notify();
                    // 输出当前线程的名称和atomicInteger的值
                    System.out.println("synchronized打印偶数---" + Thread.currentThread().getName() + "：" + atomicInteger.getAndIncrement());
                    try {
                        // 当前线程等待，直到被唤醒
                        object.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();

        // 创建另一个线程，执行另一个Lambda表达式
        new Thread(() -> {
            // 当atomicInteger的值小于等于200时，执行循环
            while (atomicInteger.get() <= 200) {
                // 使用synchronized关键字，保证线程安全
                synchronized (object) {
                    // 唤醒其他等待的线程
                    object.notify();
                    // 输出当前线程的名称和atomicInteger的值
                    System.out.println("synchronized打印奇数---" + Thread.currentThread().getName() + "：" + atomicInteger.getAndIncrement());
                    try {
                        // 当前线程等待，直到被唤醒
                        object.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }
}