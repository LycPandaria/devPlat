# 为系统添加 Log
1. 为系统添加相应的 Log 依赖，在 pom.xml 中添加
```xml
<!-- 日志文件管理包 -->
<!-- log start -->
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.6.6</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.2.12</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-log4j12</artifactId>
    <version>1.2.12</version>
</dependency>
```

2. 添加 log4j.properties 到 /resources/properites 下
```text
# 可以根据自己需求更改配置
# Global logging configuration
log4j.rootLogger=ERROR, stdout

# MyBatis logging configuration...
log4j.logger.edu.devplat=DEBUG

# Console output
log4j.appender.stdout=org.apache.log4j.ConsoleAppender
log4j.appender.stdout.layout=org.apache.log4j.PatternLayout
log4j.appender.stdout.layout.ConversionPattern=%5p [%t] - %m%n
```

3. 在 web.xml 中配置 Log 相应配置
```xml
<!-- log4g日志配置 -->
<context-param>
    <param-name>log4jConfigLocation</param-name>
    <!-- 这里的路径必须和上面的 log4j.properties 配置文件路径一致-->
    <param-value>classpath:properties/log4j.properties</param-value>
</context-param>
<listener>
    <description>log4g日志加载</description>
    <listener-class>org.springframework.web.util.Log4jConfigListener</listener-class>
</listener>
```

4. 最后在输出中变可以看到 Mybatis 的日志输出
```text
DEBUG [http-bio-8080-exec-6] - ooo Using Connection [com.mysql.jdbc.JDBC4Connection@1d8ec970]
DEBUG [http-bio-8080-exec-6] - ==>  Preparing: SELECT trunk_type, item_code, item_name FROM trunk_item WHERE trunk_type=? and item_code=? 
DEBUG [http-bio-8080-exec-6] - ==> Parameters: 1(Integer), 39(String)
```
