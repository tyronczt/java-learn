# idea使用总结

> 实际开发中一直使用eclipse，为了更有效的开发，便尝试在新的项目及自己练习的项目中使用idea开发工具，使用后便喜欢上了它，下面将idea的一些个人使用情况总结，记录之，并即时更新补充。

### 激活idea

大神之作：http://idea.lanyus.com/

### 快捷键

由于个人使用习惯，将idea的快捷键统一设置成eclipse风格的。

`File > Keymap > Eclipse `  所有的快捷键都是在Keymap里配置，你也可以根据自己习惯进行自定义。

为防止`快捷键冲突`，将输入法、QQ、音乐软件等的快捷键取消或更改。

- main方法 

  -- `psvm`

- 控制台打印

  -- `sout`

- foreach循环

  -- `iter`

- 根据类名查找类

  -- `Ctrl + Shift + Alt + N`

- 0%classes, 0% lines covered

  -- `Ctrl + Alt + F6`，然后取消勾选，中间的那个No Coverage


### 优化配置

1、为类添加自动注释模版

File-->Settings-->Editor-->File and Code Templates

```java
/**
*@Description: ${description}
*
*@Author: tyron
*@Date: Created in ${DATE}
*/
```

2、为方法添加自动注释模版
File-->Settings-->Editor-->Live Templates

点击"+"号后，选择"Templates Group…"
```java
/**
 * create by: tyron
 * description: TODO
 * create time: $date$ $time$
 * 
 $params$
 * @return $return$
 */
```
此时，在方法体内部输入add+Tab就可以生成注释了。

图文参考：https://www.cnblogs.com/mmzs/p/8858634.htm

3、IntelliJ IDEA关闭“Found duplicate code in”提示

File → Settings → Editor → Inspections；在Settings页面右侧的搜索栏处搜索 “Duplicated Code”，取消掉Duplicated Code后面的勾选，再保存设置


### 插件

#### Lombok plugin

开发神器，可以简化你的实体类，让你i不再写get/set方法，还能快速的实现builder模式，以及链式调用方法，总之就是为了简化实体类而生的插件。

Settings ---> Plugins ---> **Lombok plugin** 在线安装

http://plugins.jetbrains.com/plugin/6317-lombok-plugin

根据对应版本进行离线安装

#### Free Mybatis plugin

可以在mapper接口中和mapper的xml文件中来回跳转，就像接口跳到实现类那样简单。

Settings ---> Plugins ---> **Free Mybatis plugin** 在线安装

https://plugins.jetbrains.com/plugin/8321-free-mybatis-plugin

根据对应版本进行离线安装

