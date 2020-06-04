### 简介

**FastDFS** 是一个开源的高性能分布式文件系统（DFS）。 它的主要功能包括：文件存储，文件同步和文件访问，以及高容量和负载平衡。主要解决了海量数据存储问题，特别适合以中小文件（建议范围：4KB < file_size <500MB）为载体的在线服务。官网地址：https://github.com/happyfish100/fastdfs

FastDFS 系统有三个角色：跟踪服务器(Tracker Server)、存储服务器(Storage Server)和客户端(Client)。

　　**Tracker Server**：跟踪服务器，主要做调度工作，起到均衡的作用；负责管理所有的 storage server和 group，每个 storage 在启动后会连接 Tracker，告知自己所属 group 等信息，并保持周期性心跳。

　　**Storage Server**：存储服务器，主要提供容量和备份服务；以 group 为单位，每个 group 内可以有多台 storage server，数据互为备份。

　　**Client**：客户端，上传下载数据的服务器，也就是我们自己的项目所部署在的服务器。

### 搭建过程

软件下载（均为GitHub地址）：

[libfastcommon](https://github.com/happyfish100/libfastcommon/releases)

[fastdfs](https://github.com/happyfish100/fastdfs/releases)

[fastdfs-nginx-module](https://github.com/happyfish100/fastdfs-nginx-module/releases)

此次实验均使用最新版本：`libfastcommon-1.0.43.tar.gz`、`fastdfs-6.06.tar.gz`、`fastdfs-nginx-module-1.22.tar.gz`

#### 单机版

- **环境准备**

准备一台虚拟机，ip是：`192.168.255.100`

- **依赖环境安装**

`gcc`、`g++ `  基础环境

```shell
# yum install -y gcc gcc-c++
```

`perl` 编译环境

```shell
# yum install perl
```

`vim` 编辑软件，方便修改配置文件

```shell
# yum install yum
```



#### 集群版（三台）

准备三台虚拟机，ip是：`192.168.255.100`、`192.168.255.101`、`192.168.255.102`

![环境准备](http://tyronblog.com/upload/2020/6/%E7%8E%AF%E5%A2%83%E5%87%86%E5%A4%87-0b5cffd09976497c80f60fe982d184c9.png)

### 参考

- [用FastDFS一步步搭建文件管理系统](https://www.cnblogs.com/chiangchou/p/fastdfs.html)

- [FastDFS集群部署](https://www.cnblogs.com/cnmenglang/p/6731209.html)

