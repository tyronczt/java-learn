## 异常

### 知识点

**定义**

异常指不期而至的各种状况，如：文件找不到、网络连接失败、非法参数等。异常是一个事件，它发生在程序运行期间，干扰了正常的指令流程。Java通 过API中Throwable类的众多子类描述各种不同的异常。因而，Java异常都是对象，是**Throwable子类**的实例，描述了出现在一段编码中的 错误条件。当条件生成时，错误将引发异常。

**Java异常类层次结构图：**

![img](https://img-my.csdn.net/uploads/201211/27/1354020417_5176.jpg)

在 Java 中，所有的异常都有一个共同的祖先 **Throwable**（可抛出）。Throwable 指定代码中可用异常传播机制通过 Java 应用程序传输的任何问题的共性。

Throwable： 有两个重要的子类：Exception（异常）和 Error（错误），二者都是 Java 异常处理的重要子类，各自都包含大量子类。

- **Error（错误）**:是程序无法处理的错误，表示运行应用程序中较严重问题。大多数错误与代码编写者执行的操作无关，而表示代码运行时 JVM（Java 虚拟机）出现的问题。例如，Java虚拟机运行错误（Virtual MachineError），当 JVM 不再有继续执行操作所需的内存资源时，将出现 OutOfMemoryError。这些异常发生时，Java虚拟机（JVM）一般会选择线程终止。

这些错误表示故障发生于虚拟机自身、或者发生在虚拟机试图执行应用时，如Java虚拟机运行错误（Virtual MachineError）、类定义错误（NoClassDefFoundError）等。这些错误是不可查的，因为它们在应用程序的控制和处理能力之 外，而且绝大多数是程序运行时不允许出现的状况。对于设计合理的应用程序来说，即使确实发生了错误，本质上也不应该试图去处理它所引起的异常状况。在 Java中，错误通过Error的子类描述。

- **Exception（异常）**:是程序本身可以处理的异常。

Exception 类有一个重要的子类 RuntimeException。RuntimeException 类及其子类表示“JVM 常用操作”引发的错误。例如，若试图使用空值对象引用、除数为零或数组越界，则分别引发运行时异常（NullPointerException、ArithmeticException）和 ArrayIndexOutOfBoundException。
**注意**：异常和错误的区别：异常能被程序本身可以处理，错误是无法处理。

通常，Java的异常(包括Exception和Error)分为**可查的异常（checked exceptions）**和**不可查的异常（unchecked exceptions）**。
​      可查异常（编译器要求必须处置的异常）：正确的程序在运行中，很容易出现的、情理可容的异常状况。可查异常虽然是异常状况，但在一定程度上它的发生是可以预计的，而且一旦发生这种异常状况，就必须采取某种方式进行处理。

除了RuntimeException及其子类以外，其他的Exception类及其子类都属于可查异常。这种异常的特点是Java编译器会检查它，也就是说，当程序中可能出现这类异常，要么用try-catch语句捕获它，要么用throws子句声明抛出它，否则编译不会通过。

不可查异常(编译器不要求强制处置的异常):包括运行时异常（RuntimeException与其子类）和错误（Error）。

Exception 这种异常分两大类**运行时异常**和**非运行时异常**(编译异常)。程序中应当尽可能去处理这些异常。

运行时异常：都是RuntimeException类及其子类异常，如NullPointerException(空指针异常)、IndexOutOfBoundsException(下标越界异常)等，这些异常是不检查异常，程序中可以选择捕获处理，也可以不处理。这些异常一般是由程序逻辑错误引起的，程序应该从逻辑角度尽可能避免这类异常的发生。

运行时异常的特点是Java编译器不会检查它，也就是说，当程序中可能出现这类异常，即使没有用try-catch语句捕获它，也没有用throws子句声明抛出它，也会编译通过。
非运行时异常 （编译异常）：是RuntimeException以外的异常，类型上都属于Exception类及其子类。从程序语法角度讲是必须进行处理的异常，如果不处理，程序就不能编译通过。如IOException、SQLException等以及用户自定义的Exception异常，一般情况下不自定义检查异常。



Java异常机制用到的几个关键字：**try、catch、finally、throw、throws。**<br/>
• **try**        -- 用于监听。将要被监听的代码(可能抛出异常的代码)放在try语句块之内，当try语句块内发生异常时，异常就被抛出。<br/>
• **catch**   -- 用于捕获异常。catch用来捕获try语句块中发生的异常。<br/>
• **finally**  -- finally语句块总是会被执行。它主要用于回收在try块里打开的物力资源(如数据库连接、网络连接和磁盘文件<br/>)。只有finally块，执行完成之后，才会回来执行try或者catch块中的return或者throw语句，如果finally中使用了return或者throw等终止方法的语句，则就不会跳回执行，直接停止。<br/>
• **throw**   -- 用于抛出异常。<br/>
• **throws** -- 用在方法签名中，用于声明该方法可能抛出的异常。


### 面试题

**在Java异常处理的过程中，你遵循的那些最好的实践是什么？**

1) 调用方法的时候返回布尔值来代替返回null，这样可以 NullPointerException。由于空指针是java异常里最恶心的异常；

2) catch块里别不写代码。空catch块是异常处理里的错误事件，因为它只是捕获了异常，却没有任何处理或者提示。通常你起码要打印出异常信息，当然你最好根据需求对异常信息进行处理；

3) 能抛受控异常（checked Exception）就尽量不抛受非控异常(checked Exception)。通过去掉重复的异常处理代码，可以提高代码的可读性；

4) 绝对不要让你的数据库相关异常显示到客户端。由于绝大多数数据库和SQLException异常都是受控异常，在Java中，你应该在DAO层把异常信息处理，然后返回处理过的能让用户看懂并根据异常提示信息改正操作的异常信息；

5) 在Java中，一定要在数据库连接，数据库查询，流处理后，在finally块中调用close()方法。

 **Java中的检查型异常和非检查型异常有什么区别？**

所谓检查（**Checked**）是指**编译器**要检查这类异常，检查的目的一方面是因为该类异常的发生难以避免，另一方面就是让开发者去解决掉这类异常，所以称为必须处理（try ...catch）的异常。如果不处理这类异常，集成开发环境中的编译器一般会给出错误提示。

　　**例如：**一个读取文件的方法代码逻辑没有错误，但程序运行时可能会因为文件找不到而抛出FileNotFoundException，如果不处理这些异常，程序将来肯定会出错。所以编译器会提示你要去捕获并处理这种可能发生的异常，不处理就不能通过编译。

所谓非检查（**Unchecked**）是指**编译器**不会检查这类异常，不检查的则开发者在代码的编辑编译阶段就不是必须处理，这类异常一般可以避免，因此无需处理（try ...catch）。如果不处理这类异常，集成开发环境中的编译器也不会给出错误提示。

　　**例如：**你的程序逻辑本身有问题，比如数组越界、访问null对象，这种错误你自己是可以避免的。编译器不会强制你检查这种异常。

**JDK7中对异常处理做了什么改变？**

**你遇到过 OutOfMemoryError 错误嘛？你是怎么搞定的？**

**try、catch、finally语句块的执行顺序**

**熟悉的runtimeException子类**

1、 java.lang.ArrayIndexOutOfBoundsException
数组索引越界异常。当对数组的索引值为负数或大于等于数组大小时抛出。

2、java.lang.ArithmeticException
算术条件异常。譬如：整数除零等。

3、java.lang.NullPointerException
空指针异常。当应用试图在要求使用对象的地方使用了null时，抛出该异常。譬如：调用null对象的实例方法、访问null对象的属性、计算null对象的长度、使用throw语句抛出null等等。

4、java.lang.ClassNotFoundException
找不到类异常。当应用试图根据字符串形式的类名构造类，而在遍历CLASSPAH之后找不到对应名称的class文件时，抛出该异常。

5、java.lang.NegativeArraySizeException  数组长度为负异常 

6、java.lang.ArrayStoreException 数组中包含不兼容的值抛出的异常

7、java.lang.SecurityException 安全性异常

8、java.lang.IllegalArgumentException 非法参数异常



### 参考

[深入理解java异常处理机制](https://blog.csdn.net/hguisu/article/details/6155636)

[Java中的异常和处理详解](https://www.cnblogs.com/lulipro/p/7504267.html)

[Java异常的捕获及处理---小总结](https://blog.csdn.net/jakezhang1990/article/details/72880700)

[Java的Exception和Error面试题10问10答](https://www.oschina.net/translate/10-java-exception-and-error-interview-questions-answers-programming)

[JAVA基础——异常详解](https://www.cnblogs.com/hysum/p/7112011.html)

[Java异常(一) Java异常简介及其架构](https://www.cnblogs.com/skywang12345/p/3544168.html)

https://www.imooc.com/article/14668

https://blog.csdn.net/liyazhou0215/article/details/77413726

https://mp.weixin.qq.com/s/m-vJ6EA7b23w7ijzVtzY3g 
