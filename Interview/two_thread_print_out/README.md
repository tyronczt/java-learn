> 加深对并发编程的理解，如`synchronized 、ReentrantLock`、`Semaphore`...
>

## 1. 使用静态变量`flag`进行控制
仅通过 boolean flag 控制线程切换，逻辑简单。

```java
/**
 * 静态原子整型变量，用于在多线程环境下进行原子操作。
 * 该变量初始值为0，通过AtomicInteger类提供的原子方法，可以确保在多线程环境下的线程安全操作。
 */
private static AtomicInteger atomicInteger = new AtomicInteger(0);
/**
 * 静态变量 `flag` 是一个 volatile 修饰的布尔值。
 *
 * 该变量被声明为 `volatile`，意味着它的值对所有线程都是可见的，并且对它的修改会立即反映到主内存中，
 * 从而确保多线程环境下的可见性和一致性。`volatile` 关键字通常用于标记那些可能被多个线程同时访问的变量，
 * 以避免线程间的数据不一致问题。
 *
 * 该变量通常用于控制线程的执行状态，例如作为线程循环的退出条件。
 */
private static volatile boolean flag = true;

public static void main(String[] args) {
    // 创建一个线程，执行一个Lambda表达式
    new Thread(() -> {
        // 当atomicInteger的值小于等于200时，执行循环
        while (atomicInteger.get() <= 200) {
            // 如果flag为true，则输出当前线程的名称和atomicInteger的值，并将flag设置为false
            if (flag) {
                System.out.println("Flag打印偶数---" + Thread.currentThread().getName() + "：" + atomicInteger.getAndIncrement());
                flag = false;
            }
        }
    }).start();

    // 创建另一个线程，执行另一个Lambda表达式
    new Thread(() -> {
        // 当atomicInteger的值小于等于200时，执行循环
        while (atomicInteger.get() <= 200) {
            // 如果flag为false，则输出当前线程的名称和atomicInteger的值，并将flag设置为true
            if (!flag) {
                System.out.println("Flag打印奇数---" + Thread.currentThread().getName() + "：" + atomicInteger.getAndIncrement());
                flag = true;
            }
        }
    }).start();
}
```

此方法参考AQS的标志位：

**相同点**：

1. **volatile变量保证可见性**  
用户代码中的 `flag` 和 AQS 的 `state`（volatile int）均通过 `volatile` 保证线程间变量可见性，确保状态变化能被所有线程及时感知。
2. **线程协作控制**  
两者均通过共享变量（`flag` 或 `state`）控制线程的执行顺序或资源访问权限，实现线程间的协作。

**不同点**：

| **维度**           | **代码实现**                                                 | **AQS实现**                                                  |
| ------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| **状态管理**       | 仅通过 `boolean flag` 控制线程切换，逻辑简单。               | 通过 `volatile int state` 管理资源状态（如锁计数、信号量值），支持复杂状态（如 `getState()`、`setState()`）。 |
| **同步机制**       | 依赖忙等待（自旋循环 `while (atomicInteger.get() <= 200)`），CPU消耗较高。 | 结合 CAS（Compare-And-Swap）操作竞争资源，失败线程直接进入阻塞队列（FIFO），通过 `LockSupport.park()` 和 `unpark()` 实现高效线程阻塞/唤醒。 |
| **线程阻塞与唤醒** | 无阻塞机制，线程持续轮询 `flag` 和 `atomicInteger`。         | 线程竞争失败时进入阻塞队列，避免空转，降低 CPU 开销。唤醒时按队列顺序公平释放资源。 |
| **资源争用处理**   | 通过简单条件判断（`if (flag)`）交替执行，可能因竞争导致“漏检”或“误判”。 | 通过 `CAS` 原子操作确保资源竞争的无锁化，避免伪共享和竞态条件。 |
| **终止条件**       | 依赖 `atomicInteger` 达到阈值（200）终止循环，但未处理线程安全退出（如可能超限）。 | 提供 `tryAcquire`、`tryRelease` 等抽象方法，确保资源释放和线程终止的原子性。 |
| **通用性**         | 针对特定场景（双线程交替打印），功能单一。                   | 作为通用框架，支持多种同步器（如锁、信号量、栅栏），可灵活扩展。 |


**核心差异总结：**

+ **用户代码**：简单依赖 `volatile` 和自旋实现线程协作，适合轻量级场景，但存在性能和线程安全风险（如忙等待、可能超限）。  
+ **AQS**：基于队列 + CAS + 阻塞机制，提供高效、可扩展的线程同步方案，适用于复杂并发场景（如锁、信号量等）。

**输出结果：**

![](https://cdn.nlark.com/yuque/0/2025/png/22165187/1743681664070-fd289082-890d-4d49-91dc-839017ff84c4.png)

## 2. 使用 `synchronized` 结合 `wait()/notify()`
这是最基础的线程同步方案，通过共享锁对象和内置的等待/通知机制实现交替执行。  
实现步骤：

+ 定义一个共享计数器 `count` 和锁对象（如 `Object lock`）。
+ 线程A在 `count` 为偶数时打印并唤醒线程B；线程B在 `count` 为奇数时打印并唤醒线程A。
+ 每次打印后递增 `count`，直到达到200。

示例代码：

```java
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
```

关键点：

+ 使用 `synchronized` 确保线程安全。
+ `wait()` 释放锁并进入等待状态，`notify()` 唤醒等待线程。
+ 循环中必须用 `while` 而非 `if`，防止虚假唤醒。

**输出结果：**

![](https://cdn.nlark.com/yuque/0/2025/png/22165187/1743681980908-0d3e2139-f75e-4229-9254-cb652ebc884f.png)

## 3. 使用信号量（**`Semaphore`）**
通过信号量控制线程的执行顺序，初始信号量分配决定启动顺序。  
实现步骤：

+ 初始化两个信号量：`Semaphore semaphoreA = new Semaphore(1)`（线程A先执行），`Semaphore semaphoreB = new Semaphore(0)`。
+ 线程A打印后释放线程B的信号量，线程B打印后释放线程A的信号量。

示例代码：

```java
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
```

关键点：

+ 信号量的初始许可数控制启动顺序。
+ 线程间通过 `acquire()` 和 `release()` 交替获取执行权。

**输出结果：**

![](https://cdn.nlark.com/yuque/0/2025/png/22165187/1743682417287-852dec7c-8309-4fd8-a2b5-c8a10bb61eb3.png)

## 4. 使用 `ReentrantLock` 和 `Condition`
利用显式锁和条件变量实现更灵活的线程协作，适合复杂场景。  
实现步骤：

+ 创建 `ReentrantLock` 和两个 `Condition` 对象（如 `evenCondition` 和 `oddCondition`）。
+ 线程A在偶数时打印并唤醒线程B，线程B在奇数时打印并唤醒线程A。

示例代码：

```java


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
```

关键点：

+ `Condition` 提供更细粒度的线程等待与唤醒**。
+ 需手动释放锁（`unlock()`）以避免死锁。

**输出结果：**

![](https://cdn.nlark.com/yuque/0/2025/png/22165187/1743682734173-2902fe45-fd73-4152-8fac-b24f9b64104c.png)

## 方法对比
|      方法       |           优点           |             缺点             |
| :-------------: | :----------------------: | :--------------------------: |
|     `flag`      |    无锁设计，代码简洁    | 存在竞态条件，忙等待浪费资源 |
| `synchronized`  |  代码简单，无需额外依赖  | 灵活性较低，无法指定唤醒线程 |
|   `Semaphore`   |    逻辑清晰，易于扩展    |    需处理信号量初始值设置    |
| `ReentrantLock` | 支持多条件变量，灵活性高 |        代码复杂度较高        |


**注意事项**

1. **共享变量可见性**：需使用`volatile`或同步块确保变量修改对其他线程可见（示例中通过同步机制隐含保证）。
2. **终止条件**：循环需严格判断`count <= 200`，防止越界。
3. **异常处理**：`InterruptedException`需捕获并处理，避免线程意外终止。

以上方法均可实现需求，推荐根据场景选择：简单场景用`synchronized`，复杂同步需求用`ReentrantLock`，信号量适合明确许可控制的场景。

## 思考：可以用`CountDownLatch`实现吗？
可以，但不推荐

`CountDownLatch属于一次性计数器：初始化时指定一个固定数值（`count`），线程调用 `countDown()` 减少计数器，其他线程通过 `await()` 等待计数器归零。计数器归零后无法重复使用。

大致思路：

> 每个线程在打印后，触发对方的CountDownLatch，然后等待自己的CountDownLatch被触发。但每次循环需要重新创建CountDownLatch实例，或者使用原子操作来重置计数器。但CountDownLatch不支持重置，所以每次循环都需要新的实例，这在代码实现上可能比较复杂。
>

## 再思考，那`CyclicBarrier`呢？
可行，也可实践。

![](https://cdn.nlark.com/yuque/0/2025/png/22165187/1743684764677-6c895cdb-e067-4f83-92c9-174d61f49edc.png)

具体代码已上传Github：

## 再再思考，有CyclicBarrier、Semaphore、CountDownLatch的区别，适用场景？
参看：[CyclicBarrier、Semaphore、CountDownLatch的区别，适用场景](https://tyron.blog.csdn.net/article/details/146988731)