package com.tyron;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class PrintNum_5CyclicBarrier {

    /**
     * 静态原子整型变量，用于在多线程环境下进行原子操作。
     * 该变量初始值为0，通过AtomicInteger类提供的原子方法，可以确保在多线程环境下的线程安全操作。
     */
    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    private static final CyclicBarrier barrier = new CyclicBarrier(2);

    public static void main(String[] args) {
        // 创建一个线程，执行一个Lambda表达式
        new Thread(() -> {
            // 当atomicInteger的值小于等于200时，循环执行
            while (atomicInteger.get() <= 200) {
                if (atomicInteger.get() % 2 == 0) {
                    // 如果当前值为偶数，则打印偶数，并将原子整型变量加1
                    System.out.println("CyclicBarrier打印偶数---" + Thread.currentThread().getName() + "：" + atomicInteger.getAndIncrement());
                }
                try {
                    // 等待线程B同步
                    barrier.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        // 创建另一个线程，执行另一个Lambda表达式
        new Thread(() -> {
            // 当atomicInteger的值小于等于200时，循环执行
            while (atomicInteger.get() <= 200) {
                if (atomicInteger.get() % 2 == 1) {
                    // 如果当前值为奇数，则打印偶数，并将原子整型变量加1
                    System.out.println("CyclicBarrier打印奇数---" + Thread.currentThread().getName() + "：" + atomicInteger.getAndIncrement());
                }
                try {
                    // 等待线程A同步
                    barrier.await();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}