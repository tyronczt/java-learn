package com.tyron.core;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 项目常量
 */
public final class Constant {
    //JDBC配置，请修改为你项目的实际配置
    public static final String JDBC_URL = "jdbc:mysql://localhost:3306/zj_xzqh?useUnicode=true&characterEncoding=utf-8&useSSL=true&serverTimezone=UTC";
    public static final String JDBC_USERNAME = "root";
    public static final String JDBC_PASSWORD = "tian1013";
    public static final String JDBC_DIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";

    public static final String PROJECT_PATH = System.getProperty("user.dir");//项目在硬盘上的基础路径
    public static final String TEMPLATE_FILE_PATH = PROJECT_PATH + "/src/test/resources/generator/template";//模板位置

    public static final String JAVA_PATH = "/src/main/java"; //java文件路径
    public static final String RESOURCES_PATH = "/src/main/resources";//资源文件路径

    public static final String AUTHOR = "tyronchen";//@author
    public static final String DATE = new SimpleDateFormat("yyyy/MM/dd").format(new Date());//@date

    public static final String BASE_PACKAGE = "com.tyron";//生成代码所在的基础包名称

    public static final String MODEL_PACKAGE = ".xzqh";// 模块名称，可不填 .user

    public static final String ENTITY_PACKAGE = BASE_PACKAGE + ".pojo.entity" + MODEL_PACKAGE;//生成的Entity所在包
    public static final String MAPPER_PACKAGE = BASE_PACKAGE + ".mapper" + MODEL_PACKAGE;//生成的Mapper所在包
    public static final String SERVICE_PACKAGE = BASE_PACKAGE + ".service" + MODEL_PACKAGE;//生成的Service所在包
    public static final String SERVICE_IMPL_PACKAGE = SERVICE_PACKAGE + ".impl";//生成的ServiceImpl所在包
    public static final String CONTROLLER_PACKAGE = BASE_PACKAGE + ".controller" + MODEL_PACKAGE;//生成的Controller所在包

    public static final String MAPPER_INTERFACE_REFERENCE = BASE_PACKAGE + ".core.Mapper";//Mapper插件基础接口的完全限定名

    public static final String MAPPER_PACKAGE_CONF = BASE_PACKAGE + ".mapper";//配置文件中配置的mapper路径
    public static final String ENTITY_PACKAGE_CONF = BASE_PACKAGE + ".pojo.entity";//配置文件中配置的Entity路径

}
