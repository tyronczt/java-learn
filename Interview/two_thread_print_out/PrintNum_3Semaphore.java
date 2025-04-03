package com.tyron;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class PrintNum_3Semaphore {

    /**
     * 静态原子整型变量，用于在多线程环境下进行原子操作。
     * 该变量初始值为0，通过AtomicInteger类提供的原子方法，可以确保在多线程环境下的线程安全操作。
     */
    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    private static Semaphore semaphoreA = new Semaphore(1);
    private static Semaphore semaphoreB = new Semaphore(0);
    public static void main(String[] args) {
        // 创建一个线程，执行一个Lambda表达式
        new Thread(() -> {
            // 当atomicInteger的值小于等于200时，循环执行
            while (atomicInteger.get() <= 200) {
                try {
                    // 获取信号量A
                    semaphoreA.acquire();
                    // 输出当前线程的名称和atomicInteger的值，并将atomicInteger的值加1
                    System.out.println("Semaphore打印偶数---" +Thread.currentThread().getName() + "："  + atomicInteger.getAndIncrement());
                    // 释放信号量B
                    semaphoreB.release();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        // 创建另一个线程，执行另一个Lambda表达式
        new Thread(() -> {
            // 当atomicInteger的值小于200时，循环执行
            while (atomicInteger.get() < 200) {
                try {
                    // 获取信号量B
                    semaphoreB.acquire();
                    // 输出当前线程的名称和atomicInteger的值，并将atomicInteger的值加1
                    System.out.println("Semaphore打印奇数---" + Thread.currentThread().getName() + "："  + atomicInteger.getAndIncrement());
                    // 释放信号量A
                    semaphoreA.release();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}