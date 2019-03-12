# HBase学习

[TOC]

## 一、HBase介绍

### 1、HBase简介

HBase是Hadoop Database的简称 。

[Apache HBase](https://hbase.apache.org/)官方给出了这样的定义：

> [Apache](http://www.apache.org/) HBase™ is the [Hadoop](http://hadoop.apache.org/) database, a distributed, scalable, big data store.
>
> Apache HBase™是Hadoop数据库，是一个分布式，可扩展的大数据存储。
>
> 当您需要对大数据进行随机，实时读/写访问时，请使用Apache HBase™。 该项目的目标是托管非常大的表 - 数十亿行 X 百万列 - 在商品硬件集群上。 Apache HBase是一个开源的，分布式的，版本化的非关系数据库，模仿Google的Bigtable：Chang等人的结构化数据分布式存储系统。 正如Bigtable利用Google文件系统提供的分布式数据存储一样，Apache HBase在Hadoop和HDFS之上提供类似Bigtable的功能。

HBase是分布式、面向列族的开源数据库。HDFS为HBase提供可靠的底层数据存储服务，MapReduce为HBase提供高性能的计算能力，Zookeeper为HBase提供稳定服务和Failover机制，因此我们说HBase是一个通过大量廉价的机器解决海量数据的高速存储和读取的分布式数据库解决方案。

### 2、HBase的特点

**2.1、海量存储**

**2.2、列式存储**

**2.3、极易扩展**

**2.4、高并发**

**2.5、稀疏**







## 参考资料

1、[HBase 技术细节笔记](https://cloud.tencent.com/developer/article/1006043)

2、[一条数据的HBase之旅，简明HBase入门教程-开篇](https://blog.csdn.net/nosqlnotes/article/details/79647096)

3、[HBase 深入浅出](https://www.ibm.com/developerworks/cn/analytics/library/ba-cn-bigdata-HBase/index.html)

4、[《HBase 不睡觉》第一章 - 初识 HBase](https://juejin.im/post/5bf420095188253b6e5c21ed)

