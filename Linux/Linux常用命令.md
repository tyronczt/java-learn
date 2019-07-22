
## Linux常用命令.md

#### Linux文件和目录管理

- **cd 命令**

cd 命令，是 Change Directory 的缩写，用来切换工作目录。

  基本格式如下：

```shell
[root@localhost ~]# cd [相对路径或绝对路径]
```

  常用用法：

```shell
[root@localhost ~]# cd /usr/local/src
[root@localhost src]#
#进入/usr/local/src目录
[root@localhost src]# cd ~
[root@localhost ~]#
#表示回到自己的主目录，对于 root 用户，其主目录为 /root
[root@localhost src]# cd ..
[root@localhost local]# 
#表示切换到目前的上一级目录，src的上级目录是local
[root@localhost local]# cd -
/usr/local/src
[root@localhost src]# 
#表示回到刚刚的那个目录，又回到了 /usr/local/src 目录
[root@localhost src]# pwd
/usr/local/src
[root@localhost src]# 
#pwd是査看当前所在目录的命令
```

- **pwd 命令**

pwd 命令，是 Print Working Directory （打印工作目录）的缩写，功能是显示用户当前所处的工作目录。

```shell
[root@localhost ~]# pwd
/root
```

- **mkdir 命令**

mkdir 命令，是 make directories 的缩写，用于创建新目录，此命令所有用户都可以使用。

  **-m 选项** 用于手动配置所创建目录的权限，而不再使用默认权限。

  **-p  选项 **递归创建所有目录，以创建 /home/test/demo 为例，在默认情况下，你需要一层一层的创建各个目录，而使用 -p 选项，则系统会自动帮你创建 /home、/home/test 以及 /home/test/demo。

```shell
[root@localhost ~]# mkdir [-mp] 目录名

[root@localhost ~]# mkdir /root/tyron/test
mkdir: 无法创建目录"/root/tyron/test": 没有那个文件或目录
[root@localhost ~]# mkdir -p  /root/tyron/test
```

- **rmdir 命令**

rmdir 命令，是remove empty directories 的缩写，用于删除空目录。

-p 选项用于递归删除空目录。

```shell
[root@localhost ~]#rmdir -p /root/tyron/test
#rmdir 命令的作用十分有限，因为只能刪除空目录，所以一旦目录中有内容，就会报错
[root@localhost test]# pwd
/root/tyron/test
[root@localhost test]# rmdir -p /root/tyron/test
rmdir: 删除目录 "/root" 失败: 目录非空
```

- **touch命令**

touch 命令不光可以用来创建文件（当指定操作文件不存在时，该命令会在当前位置建立一个空文件），此命令更重要的功能是修改文件的时间参数（但当文件存在时，会修改此文件的时间参数）

```shell
[root@localhost ~]# touch [选项] 文件名
```

​	**-a**：只修改文件的访问时间，或–time=atime或–time=access或–time=use

​	**-c**：仅修改文件的时间参数（3 个时间参数都改变），如果文件不存在，则不建立新文件。

​	**-d**：后面可以跟欲修订的日期，而不用当前的日期，即把文件的 atime 和 mtime 时间改为指定的时间。

​	**-m**：只修改文件的数据修改时间。

​	**-t**：命令后面可以跟欲修订的时间，而不用目前的时间，时间书写格式为 `YYMMDDhhmm`。

```shell
[root@localhost ~]#touch bols
#建立名为 bols 的空文件

[root@localhost ~]# ll --time=atime bols
#查看文件的访问时间
-rw-r--r--. 1 root root 0 7月  22 23:33 bols
#文件上次的访问时间为 7 月 22 号 23：33
[root@localhost ~]#touch bols   || touch --time=atime bols
[root@localhost ~]#ll --time=atime bols
-rw-r--r--. 1 root root 0 7月  22 23:38 bols
#而如果文件已经存在，则也不会报错，只是会修改文件的访问时间。
```

- **cp命令**

cp 命令，主要用来复制文件和目录，同时借助某些选项，还可以实现复制整个目录，以及比对两文件的新旧而予以升级等功能。

```shell
[root@localhost ~]# cp [选项] 源文件 目标文件
```

​	**-a**：相当于 -d、-p、-r 选项的集合，这几个选项我们一一介绍；

​	**-d**：如果源文件为软链接（对硬链接无效），则复制出的目标文件也为软链接；

​	**-i**：询问，如果目标文件已经存在，则会询问是否覆盖；

​	**-l**：把目标文件建立为源文件的硬链接文件，而不是复制源文件；

​	**-s**：把目标文件建立为源文件的软链接文件，而不是复制源文件；

​	**-p**：复制后目标文件保留源文件的属性（包括所有者、所属组、权限和时间）；

​	**-r**：递归复制，用于复制目录；

​	**-u**：若目标文件比源文件有差异，则使用该选项可以更新目标文件，此选项可用于对文件的升级和备用。

cp 命令既可以复制文件，也可以复制目录。

```shell
[root@localhost ~]# touch copy
#建立源文件
[root@localhost ~]# cp copy /tmp/
#把源文件不改名复制到 /tmp/ 目录下

#如果需要改名复制
[root@localhost ~]# cp copy /temp/copytmp
```

- **rm命令**

rm 是强大的删除命令，它可以永久性地删除文件系统中指定的文件或目录。

```shell
[root@localhost ~]# rm[选项] 文件或目录
```

​	**-f**：强制删除（force），和 -i 选项相反，使用 -f，系统将不再询问，而是直接删除目标文件或目录。

​	**-i**：和 -f 正好相反，在删除文件或目录之前，系统会给出提示信息，使用 -i 可以有效防止不小心删除有用的文件或目录。

​	**-r**：递归删除，主要用于删除目录，可删除指定目录及包含的所有内容，包括所有的子目录和文件。

```shell
[root@localhost temp]# rm copy
rm：是否删除普通空文件 "copy"？y
[root@localhost temp]# 

# 如果需要删除目录，则需要使用"-r"选项
[root@localhost /]# rm -r temp
rm：是否进入目录"temp"? y
rm：是否删除普通空文件 "temp/copytmp"？y
rm：是否删除目录 "temp"？y
[root@localhost /]#

#强制删除,在testrm文件夹下新建temp/haha目录
[root@localhost testrm]# mkdir -p temp/haha
[root@localhost testrm]# ll
总用量 0
drwxr-xr-x. 3 root root 18 7月  23 00:21 temp
[root@localhost testrm]# cd temp/haha
[root@localhost haha]# ll
总用量 0
# 在/testrm/temp/haha 目录下新建文件heihei
[root@localhost haha]# touch heihei
[root@localhost haha]# ll
总用量 0
-rw-r--r--. 1 root root 0 7月  23 00:22 heihei
[root@localhost haha]# cd /testrm/
# 递归强制删除temp文件夹
[root@localhost testrm]# rm -rf temp/
[root@localhost testrm]# ll
总用量 0
[root@localhost testrm]# 
```

- **mv 命令**

mv 命令（move 的缩写），既可以在不同的目录之间移动文件或目录，也可以对文件和目录进行重命名。

```shell
[root@localhost ~]# mv 【选项】 源文件 目标文件
```

​	**-f**：强制覆盖，如果目标文件已经存在，则不询问，直接强制覆盖；

​	**-i**：交互移动，如果目标文件已经存在，则询问用户是否覆盖（默认选项）；

​	**-n**：如果目标文件已经存在，则不会覆盖移动，而且不询问用户；

​	**-v**：显示文件或目录的移动过程；

​	**-u**：若目标文件已经存在，但两者相比，源文件更新，则会对目标文件进行升级 。

*需要注意的是，同 rm 命令类似，mv 命令也是一个具有破坏性的命令，如果使用不当，很可能给系统带来灾难性的后果。*

#### 附录

Linux 系统中，每个文件主要拥有 3 个时间参数（通过 stat 命令进行查看），分别是文件的访问时间、数据修改时间以及状态修改时间：

- 访问时间（Access Time，简称 `atime`）：只要文件的内容被读取，访问时间就会更新。例如，使用 cat 命令可以查看文件的内容，此时文件的访问时间就会发生改变。
- 数据修改时间（Modify Time，简称 `mtime`）：当文件的内容数据发生改变，此文件的数据修改时间就会跟着相应改变。
- 状态修改时间（Change Time，简称 `ctime`）：当文件的状态发生变化，就会相应改变这个时间。比如说，如果文件的权限或者属性发生改变，此时间就会相应改变。

#### 参考文档

- [Linux学习教程，Linux入门教程（超详细）](http://c.biancheng.net/linux_tutorial/)
