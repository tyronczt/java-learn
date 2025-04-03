package com.tyron;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PrintNum_4Lock {

    /**
     * 静态原子整型变量，用于在多线程环境下进行原子操作。
     * 该变量初始值为0，通过AtomicInteger类提供的原子方法，可以确保在多线程环境下的线程安全操作。
     */
    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    private static ReentrantLock lock = new ReentrantLock();
    private static Condition evenCondition = lock.newCondition();
    private static Condition oddCondition = lock.newCondition();

    public static void main(String[] args) {
        new Thread(() -> {
            while (atomicInteger.get() <= 200) {
                lock.lock();
                try {
                    if (atomicInteger.get() % 2 == 0) {
                        // 如果当前值为偶数，则打印偶数，并将原子整型变量加1
                        System.out.println("ReentrantLock打印偶数---" + Thread.currentThread().getName() + "：" + atomicInteger.getAndIncrement());
                        // 唤醒等待的奇数线程
                        oddCondition.signal();
                    } else {
                        // 如果当前值为奇数，则等待
                        evenCondition.await();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }).start();

        new Thread(() -> {
            while (atomicInteger.get() <= 200) {
                lock.lock();
                try {
                    if (atomicInteger.get() % 2 == 1) {
                        // 如果当前值为奇数，则打印奇数，并将原子整型变量加1
                        System.out.println("ReentrantLock打印奇数---" + Thread.currentThread().getName() + "：" + atomicInteger.getAndIncrement());
                        // 唤醒等待的偶数线程
                        evenCondition.signal();
                    } else {
                        // 如果当前值为偶数，则等待
                        oddCondition.await();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }).start();
    }
}