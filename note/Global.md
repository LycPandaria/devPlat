很多时候，系统里有需要有个 Constant 类或者 Global 类之类的静态类，方便程序在运行过程中
获取一些参数设置，静态变量，常量等信息。

这些信息如果写死在其他地方，将会使得今后的维护难度增大，耦合性也会上升。

在此记录下该系统的 Global 类的基本设置。

## 新建 Global 类
这个 Global 类应该是在程序中唯一的，并且声明周期应该是 Application 级别的，
这样就应该能想到把它定义为一个单例。

```java
/**
 * 全局配置类 懒汉式单例类.在第一次调用的时候实例化自己
 * @author lyc
 *
 */
public class Global {

	private Global() {}

	/**
	 * 当前对象实例
	 */
	private static Global global = null;

	/**
	 * 静态工厂方法 获取当前对象实例 多线程安全单例模式(使用双重同步锁)
	 */

	public static synchronized Global getInstance() {

		if (global == null) {
			synchronized (Global.class) {
				if (global == null)
					global = new Global();
			}
		}
		return global;
	}

}
```
## 添加常量
初次之外，Global 类中经常放置一些常量，方便管理，也避免硬编码。
```java
/**
* 是/否
*/
public static final String YES = "1";
public static final String NO = "0";

/**
 * 对/错
 */
public static final String TRUE = "true";
public static final String FALSE = "false";
```

## 获取配置
Global 还有一个重要的作用是获取配置文件中的属性。

首先需要创建一个 PropertiesLoader 来找到配置文件。
```java
/**
* 属性文件加载对象，将读取过的配置放入其中，避免每次都去配置文件中找，加快速度
*/
private static PropertiesLoader loader = new PropertiesLoader("properties/dev_plat.properties");
```
以下列出了一些系统中用到的配置
```java
/**
 * 保存全局属性值
 */
private static Map<String, String> map = Maps.newHashMap();

/**
 * 获取配置
 * 
 * @see ${fns:getConfig('adminPath')}
 */
public static String getConfig(String key) {
    // 从map中尝试获取参数，避免每次都去配置文件中找
    String value = map.get(key);
    if (value == null) {
        value = loader.getProperty(key);
        // 如果这个属性是第一次加载，就放入到 map 中
        map.put(key, value != null ? value : StringUtils.EMPTY);
    }
    return value;
}

/**
 * 获取管理端根路径
 */
public static String getAdminPath() {
    return getConfig("adminPath");
}

/**
 * 获取前端根路径
 */
public static String getFrontPath() {
    return getConfig("frontPath");
}

/**
 * 获取URL后缀
 */
public static String getUrlSuffix() {
    return getConfig("urlSuffix");
}
```

## 使用
在其他代码中，便可以使用 Global.getAdminPath() 等方法获取配置文件中设定的管理端根路径等。

同时，也可以借助 [tld](./tlds.md) 等使得在页面上也能调用到，例如：
```xml
  <function>
    <description>获取管理路径</description>
    <name>getAdminPath</name>
    <function-class>edu.devplat.common.config.Global</function-class>
    <function-signature>java.lang.String getAdminPath()</function-signature>
    <example>${fns:getAdminPath()}</example>
  </function>
```
