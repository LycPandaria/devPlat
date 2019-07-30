# devPlat

SpringMVC + Spring + Mybatis Java开发平台平台

## 初始构建
该项目的最初始构建始于：[eclipse下SpringMVC+Maven+Mybatis+MySQL项目搭建](https://github.com/LycPandaria/simple_server/blob/master/demo_springmvc+mybatis+maven.md)

之后会慢慢为其增加各类特性

## 增加日志
[Log](./note/log.md)

## 系统 Global 常量类解析
[Global](./note/Global.md)

## jsp-tld
- [tlds](./note/tlds.md) 介绍 tld 文件在系统中的应用和原理
## 运行
1. fork() 或者 下载
2. 导入eclipse 或者 IntelliJ 中
3. 因为使用了maven架构，需要按自己的实际情况设置maven路径等

**以下是在 eclipse 中需要注意的问题**
4. 检查 buildpath 看看还有没有缺少包的情况，并做相应调整，一般来说需要加入Tomcat的包（BuildPath -> Add Libraries -> Server Runtime -> Tomcat x.0）
5. 数据库的建表语句在src/main/resources/db下，需要mysql的支持
6. Run As -> Tomcat. 需要自己的eclipse装有tomcat,若没有可以百度一下，也很简单

**以下是在 IntelliJ 中需要注意的问题**
4. File -> Project Structure 中看看JDK版本之类的错误
5. 配置Tomcat （具体可以百度）
6. 运行
