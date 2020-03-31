代码中经常能看到

```java
@RequestMapping(value = "${adminPath}/sys/menu")
```

Java代码中并没有出现过 @PathVariable 或者 @Value 注解，那为什么系统能解析 ${adminPath}

原因是在 spring-mvc 配置文件中已经引用了配置文件：

```xml
<!-- 加载配置属性文件 -->
<context:property-placeholder ignore-unresolvable="true" location="classpath:/properties/dev_plat.properties" />
```

当然，如果是在 Java 代码中进行注入，还是需要 @Value 注解
```java
@Value("${adminPath}")
private String adminPath;
```
