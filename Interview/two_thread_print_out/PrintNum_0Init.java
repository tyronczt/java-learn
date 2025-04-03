package com.tyron;

import java.util.concurrent.atomic.AtomicInteger;

public class PrintNum_0Init {

    /**
     * 静态原子整型变量，用于在多线程环境下进行原子操作。
     * 该变量初始值为0，通过AtomicInteger类提供的原子方法，可以确保在多线程环境下的线程安全操作。
     */
    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    public static void main(String[] args) {
        new Thread(() -> {
            while (atomicInteger.get() <= 200) {
                System.out.println(Thread.currentThread().getName() + ":" + atomicInteger.getAndIncrement());
            }
        }).start();

        new Thread(() -> {
            while (atomicInteger.get() <= 200) {
                System.out.println(Thread.currentThread().getName() + ":" + atomicInteger.getAndIncrement());
            }
        }).start();
    }
}