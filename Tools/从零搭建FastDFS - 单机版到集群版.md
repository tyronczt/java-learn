## 一、简介

**FastDFS** 是一个开源的高性能分布式文件系统（DFS）。 它的主要功能包括：文件存储，文件同步和文件访问，以及高容量和负载平衡。主要解决了海量数据存储问题，特别适合以中小文件（建议范围：4KB < file_size <500MB）为载体的在线服务。官网地址：https://github.com/happyfish100/fastdfs

FastDFS 系统有三个角色：跟踪服务器(Tracker Server)、存储服务器(Storage Server)和客户端(Client)。

　　**Tracker Server**：跟踪服务器，主要做调度工作，起到均衡的作用；负责管理所有的 storage server和 group，每个 storage 在启动后会连接 Tracker，告知自己所属 group 等信息，并保持周期性心跳。

　　**Storage Server**：存储服务器，主要提供容量和备份服务；以 group 为单位，每个 group 内可以有多台 storage server，数据互为备份。

　　**Client**：客户端，上传下载数据的服务器，也就是我们自己的项目所部署在的服务器。

## 二、搭建过程

软件下载（均为GitHub地址）：

[libfastcommon](https://github.com/happyfish100/libfastcommon/releases) | fastsds的库文件，需要先编译安装好

[fastdfs](https://github.com/happyfish100/fastdfs/releases)  | fastsds主文件

[fastdfs-nginx-module](https://github.com/happyfish100/fastdfs-nginx-module/releases) | fastdfs的nginx模块模块

此次实验均使用最新版本：`libfastcommon-1.0.43.tar.gz`、`fastdfs-6.06.tar.gz`、`fastdfs-nginx-module-1.22.tar.gz`

### 1、单机版

#### 1.1、环境准备

准备一台虚拟机，ip是：`192.168.255.100`

#### 1.2、依赖环境安装

> 依赖环境安装提前安装，少走弯路！！！

`gcc`、`g++ `  基础环境

```shell
# yum -y install -y gcc gcc-c++
```

`perl` 编译环境

```shell
# yum -y install perl
```

`vim` 编辑软件，方便修改配置文件

```shell
# yum -y install vim
```

`wget` 下载软件

```shell
# yum -y install wget
```

#### 1.3、FastDFS安装

##### 1.3.1、libfastcommon 安装

`libfastcommon`是从`FastDFS`和`FastDHT`中提取出来的公共C函数库，`FastDFS`依赖该模块，需要首先安装它。

```shell
解压缩并进入解压缩后的文件夹的根目录：
# tar zxvf libfastcommon-1.0.43.tar.gz && cd libfastcommon-1.0.43
执行编译安装：
# ./make.sh && ./make.sh install
```

安装完成后，返回到上一级目录（软件包所在的根目录）

##### 1.3.2、fastdfs 安装

```shell
解压缩并进入解压缩后的文件夹的根目录：
# tar zxvf fastdfs-6.06.tar.gz && cd fastdfs-6.06
执行编译安装：
# ./make.sh && ./make.sh install
```

报错：https://github.com/happyfish100/fastdfs/issues/392，同样问题，并解决。

安装成功后，在系统`/etc`路径下会生成一个`/fdfs`目录，该目录下是`FastDFS`相关的一些示例配置文件内容。

```shell
[root@localhost fastdfs-6.06]#  ll /etc/fdfs/
总用量 32
-rw-r--r--. 1 root root  1909 6月   4 22:53 client.conf.sample
-rw-r--r--. 1 root root 10246 6月   4 22:53 storage.conf.sample
-rw-r--r--. 1 root root   620 6月   4 22:53 storage_ids.conf.sample
-rw-r--r--. 1 root root  9138 6月   4 22:53 tracker.conf.sample
```

- ##### tracker 配置

```shell
将/etc/fdfs/tracker.conf.sample重命名为/etc/fdfs/tracker.conf并编辑它：
# mv /etc/fdfs/tracker.conf.sample /etc/fdfs/tracker.conf && vim /etc/fdfs/tracker.conf
修改配置：
# the base path to store data and log files
base_path=/home/tyron/fastdfs/tracker
创建对应文件夹：
# mkdir -p /home/tyron/fastdfs/tracker

复制配置文件
cp /opt/fastdfs/fastdfs-6.06/conf/http.conf /etc/fdfs/http.conf
cp /opt/fastdfs/fastdfs-6.06/conf/mime.types /etc/fdfs/mime.types
```

- ##### storage 配置

```shell
将/etc/fdfs/storage.conf.sample 重命名为/etc/fdfs/storage.conf 并编辑它：
# mv /etc/fdfs/storage.conf.sample /etc/fdfs/storage.conf && vim /etc/fdfs/storage.conf

找到`base_path`的配置行，将其修改为：
base_path=/home/tyron/fastdfs/storage
找到`store_path0`的配置行，将其修改为：
store_path0=/home/tyron/fastdfs/storage
找到`tracker_server`的配置行，将其修改为`tracker`服务器的地址：
tracker_server=192.168.255.100:22122

创建对应文件夹：
# mkdir -p /home/tyron/fastdfs/storage
```

> `store_path0`如果配置的路径不存在，则会使用`base_path`配置的路径。

- ##### 启动`tracker` 和 `storage`

```shell
service fdfs_trackerd start
service fdfs_storaged start

# 如果
/usr/bin/fdfs_trackerd /etc/fdfs/tracker.conf start
/usr/bin/fdfs_storaged /etc/fdfs/storage.conf start

# 查看进程状态
ps -aux | grep fdfs
```

![启动信息](http://tyronblog.com/upload/2020/6/%E5%90%AF%E5%8A%A8%E4%BF%A1%E6%81%AF-09709c56e09f4d47aa8b1d1f0b5d2820.png)

显示：storage启动正常，tracker无法启动

查看日志信息：

```shell
# cat /home/tyron/fastdfs/tracker/logs/trackerd.log
/usr/bin/fdfs_trackerd: symbol lookup error: /usr/bin/fdfs_trackerd: undefined symbol: int2str
```

软链接有问题，参考解决方式：https://blog.csdn.net/Mabanana/article/details/88076465

![tracker无法启动](http://tyronblog.com/upload/2020/6/tracker%E6%97%A0%E6%B3%95%E5%90%AF%E5%8A%A8-44be455fd46b497e8be42f230949ea3a.png)

正常启动页面，至此成功一半了！！！

![启动正常](http://tyronblog.com/upload/2020/6/%E5%90%AF%E5%8A%A8%E6%AD%A3%E5%B8%B8-abfeb50e76f745fbaff1c8704be17ebe.png)

- ##### client 配置，测试是否安装成功

```shell
将 `/etc/fdfs/client.conf.sample` 重命名为 `/etc/fdfs/client.conf` 并编辑它：
mv /etc/fdfs/client.conf.sample /etc/fdfs/client.conf && vim /etc/fdfs/client.conf

找到 `base_path`的配置行，将其修改为：
base_path=/home/tyron/fastdfs/storage
找到 `tracker_server`的配置行，将其修改为tracker服务器的地址：
tracker_server=192.168.255.100:22122
```

> 这里的base_path是storage用于存储数据文件（其路径为：base_path/data）以及日志文件（其路径为：base_path/logs）的基路径。

- ##### 上传文件测试

```shell
#fdfs_upload_file /etc/fdfs/client.conf 上传的图片

#栗子 1.jpg为自己上传的图片
# fdfs_upload_file /etc/fdfs/client.conf /opt/fastdfs/1.jpg
group0/M00/00/00/wKj_ZF7ZGFSAdRpNAAKnQDcoJRc237.jpg
```

如果返回类似的文件id则说明文件上传成功！

##### 1.3.3、fastdfs-nginx-module 安装

```shell
解压缩并进入解压缩后的文件夹的根目录：
# tar zxvf fastdfs-nginx-module-1.22.tar.gz && cd fastdfs-nginx-module-1.22
编辑src/mod_fastdfs.conf：
# vim src/mod_fastdfs.conf

找到 `url_have_group_name` 的配置行，将其改为：
url_have_group_name = true
找到 `store_path0` 的配置行，将其改为：
store_path0=/home/tyron/fastdfs/storage

复制文件 `src/mod_fastdfs.conf` 到 `/etc/fdfs/` 目录下：
cp src/mod_fastdfs.conf /etc/fdfs/

编辑 `src/config` ：
# vim src/config

找到 `CORE_INCS` 的配置行：
CORE_INCS="$CORE_INCS /usr/local/include"
去掉路径中的 `/local` ，改为如下并保存退出：
CORE_INCS="$CORE_INCS /usr/include/"
```

> 如果请求的url地址中包含了group_name（如“group1/M00/00/00/xxx”）则应设为true。
> 如果请求的url地址中不包含group_name（如“/M00/00/00/xxx”）则应设为false。
>
> 必须配置与本机 `storage.conf` 中配置的 ` store_path0` 路径相同。

##### 1.3.4、安装Nginx（线上）

```shell
一键安装依赖
# yum -y install gcc zlib zlib-devel pcre-devel openssl openssl-devel

 创建一个文件夹
# mkdir -p /usr/local/nginx  && cd /usr/local/nginx

下载tar包
# wget http://nginx.org/download/nginx-1.16.1.tar.gz

解压
# tar -xvf nginx-1.16.1.tar.gz

编译nginx，并安装
# cd /usr/local/nginx/nginx-1.16.1
# ./configure --prefix=/usr/local/nginx --add-module=/opt/fastdfs/fastdfs-nginx-module-1.22/src && make && make install

验证是否安装成功
# /usr/local/nginx/sbin/nginx -v
nginx version: nginx/1.16.1
```

出现版本信息，即表示Nginx安装成功！

```shell
修改Nginx配置
# vim /usr/local/nginx/conf/nginx.conf

配置信息如下：
server {
	listen 80;
	server_name localhost;
	location /group0/M00 {
		root /home/tyron/fastdfs/storage/data/;
    	ngx_fastdfs_module;
	}
	error_page  500 502 503 504 /50x.html;
	location = /50x.html {
		root  html;
	}
}

启动Nginx
# /usr/local/nginx/sbin/nginx
ngx_http_fastdfs_set pid=43651
```

如果请求的url地址中包含了group_name（如“group1/M00/00/00/xxx”）则应设为true。
如果请求的url地址中不包含group_name（如“/M00/00/00/xxx”）则应设为false。

#### 集群版（三台）

准备三台虚拟机，ip是：`192.168.255.100`、`192.168.255.101`、`192.168.255.102`

![环境准备](http://tyronblog.com/upload/2020/6/%E7%8E%AF%E5%A2%83%E5%87%86%E5%A4%87-0b5cffd09976497c80f60fe982d184c9.png)





### 参考

- [用FastDFS一步步搭建文件管理系统](https://www.cnblogs.com/chiangchou/p/fastdfs.html)

- [FastDFS集群部署](https://www.cnblogs.com/cnmenglang/p/6731209.html)

