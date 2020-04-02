此文主要介绍项目中的权限控制工具 Shiro 以及在项目中是如何使用的。

**这里并不能展示所有的代码，只是做讲解 Shiro 如何在系统中发挥作用，具体的源码在 src 中查看**

# Shiro 简介
![shiro1.png](../pic/shiro1.png)

- **Authentication**：身份认证 / 登录，验证用户是不是拥有相应的身份；

- **Authorization**：授权，即权限验证，验证某个已认证的用户是否拥有某个权限；即判断用户是否能做事情，常见的如：验证某个用户是否拥有某个角色。或者细粒度的验证某个用户对某个资源是否具有某个权限；

- **Session Manager**：会话管理，即用户登录后就是一次会话，在没有退出之前，它的所有信息都在会话中；会话可以是普通 JavaSE 环境的，也可以是如 Web 环境的；

- **Cryptography**：加密，保护数据的安全性，如密码加密存储到数据库，而不是明文存储；

- Web Support：Web 支持，可以非常容易的集成到 Web 环境；

- **Caching**：缓存，比如用户登录后，其用户信息、拥有的角色 / 权限不必每次去查，这样可以提高效率；

- Concurrency：shiro 支持多线程应用的并发验证，即如在一个线程中开启另一个线程，能把权限自动传播过去；

- Testing：提供测试支持；

- Run As：允许一个用户假装为另一个用户（如果他们允许）的身份进行访问；

- **Remember Me**：记住我，这个是非常常见的功能，即一次登录后，下次再来的话不用登录了。

记住一点，Shiro 不会去维护用户、维护权限；这些需要我们自己去设计 / 提供；然后通过相应的接口注入给 Shiro 即可。

## 外部结构
![shiro2.png](../pic/shiro2.png)

应用代码直接交互的对象是 Subject，也就是说 Shiro 的对外 API 核心就是 Subject；其每个 API 的含义：

- **Subject**：主体，代表了当前 “用户”，这个用户不一定是一个具体的人，与当前应用交互的任何东西都是 Subject，如网络爬虫，机器人等；即一个抽象概念；所有 Subject 都绑定到 SecurityManager，与 Subject 的所有交互都会委托给 SecurityManager；可以把 Subject 认为是一个门面；SecurityManager 才是实际的执行者；

- **SecurityManager**：安全管理器；即所有与安全有关的操作都会与 SecurityManager 交互；且它管理着所有 Subject；可以看出它是 Shiro 的核心，它负责与后边介绍的其他组件进行交互，如果学习过 SpringMVC，你可以把它看成 DispatcherServlet 前端控制器；

- **Realm**：域，Shiro 从从 Realm 获取安全数据（如用户、角色、权限），就是说 SecurityManager 要验证用户身份，那么它需要从 Realm 获取相应的用户进行比较以确定用户身份是否合法；也需要从 Realm 得到用户相应的角色 / 权限进行验证用户是否能进行操作；可以把 Realm 看成 DataSource，即安全数据源。

也就是说对于我们而言，最简单的一个 Shiro 应用：

1. 应用代码通过 Subject 来进行认证和授权，而 Subject 又委托给 SecurityManager；

2. 我们需要给 Shiro 的 SecurityManager 注入 Realm，从而让 SecurityManager 能得到合法的用户及其权限进行判断。

从以上也可以看出，Shiro 不提供维护用户 / 权限，而是通过 Realm 让开发人员自己注入。

## 内部结构
![shiro3.png](../pic/shiro3.png)
- Subject：主体，可以看到主体可以是任何可以与应用交互的 “用户”；

- SecurityManager：相当于 SpringMVC 中的 DispatcherServlet 或者 Struts2 中的 FilterDispatcher；是 Shiro 的心脏；所有具体的交互都通过 SecurityManager 进行控制；它管理着所有 Subject、且负责进行认证和授权、及会话、缓存的管理。

- Authenticator：认证器，负责主体认证的，这是一个扩展点，如果用户觉得 Shiro 默认的不好，可以自定义实现；其需要认证策略（Authentication Strategy），即什么情况下算用户认证通过了；

- Authrizer：授权器，或者访问控制器，用来决定主体是否有权限进行相应的操作；即控制着用户能访问应用中的哪些功能；

- Realm：可以有 1 个或多个 Realm，可以认为是安全实体数据源，即用于获取安全实体的；可以是 JDBC 实现，也可以是 LDAP 实现，或者内存实现等等；由用户提供；注意：Shiro 不知道你的用户 / 权限存储在哪及以何种格式存储；所以我们一般在应用中都需要实现自己的 Realm；

- SessionManager：如果写过 Servlet 就应该知道 Session 的概念，Session 呢需要有人去管理它的生命周期，这个组件就是 SessionManager；而 Shiro 并不仅仅可以用在 Web 环境，也可以用在如普通的 JavaSE 环境、EJB 等环境；所有呢，Shiro 就抽象了一个自己的 Session 来管理主体与应用之间交互的数据；这样的话，比如我们在 Web 环境用，刚开始是一台 Web 服务器；接着又上了台 EJB 服务器；这时想把两台服务器的会话数据放到一个地方，这个时候就可以实现自己的分布式会话（如把数据放到 Memcached 服务器）；

- SessionDAO：DAO 大家都用过，数据访问对象，用于会话的 CRUD，比如我们想把 Session 保存到数据库，那么可以实现自己的 SessionDAO，通过如 JDBC 写到数据库；比如想把 Session 放到 Memcached 中，可以实现自己的 Memcached SessionDAO；另外 SessionDAO 中可以使用 Cache 进行缓存，以提高性能；

- CacheManager：缓存控制器，来管理如用户、角色、权限等的缓存的；因为这些数据基本上很少去改变，放到缓存中后可以提高访问的性能

- Cryptography：密码模块，Shiro 提供了一些常见的加密组件用于如密码加密 / 解密的。

# Shiro-Spring 配置

## maven
该项目使用了 Maven 作为依赖管理，用 Maven 便可以轻松添加 Shiro 依赖：
```xml
<properties>
    <!-- 其它依赖 --> 
    <shiro.version>1.2.3</shiro.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.apache.shiro</groupId>
        <artifactId>shiro-core</artifactId>
        <version>${shiro.version}</version>
    </dependency>
    <dependency>
        <groupId>org.apache.shiro</groupId>
        <artifactId>shiro-spring</artifactId>
        <version>${shiro.version}</version>
    </dependency>
    <dependency>
        <groupId>org.apache.shiro</groupId>
        <artifactId>shiro-cas</artifactId>
        <version>${shiro.version}</version>
    </dependency>
    <dependency>
        <groupId>org.apache.shiro</groupId>
        <artifactId>shiro-web</artifactId>
        <version>${shiro.version}</version>
    </dependency>
    <dependency>
        <groupId>org.apache.shiro</groupId>
        <artifactId>shiro-ehcache</artifactId>
        <version>${shiro.version}</version>
    </dependency>
</dependencies>
```

## 配置文件
增加 application-shiro.xml 作为 Shiro 的主要配置文件，以下主要是解释配置的作用：
```xml
<bean id="securityManager" class="org.apache.shiro.web.mgt.DefaultWebSecurityManager">
        <!-- 注入realm -->
        <property name="realm" ref="systemAuthorizingRealm" />
        <!-- 注入session管理器 -->
        <property name="sessionManager" ref="sessionManager" />
        <!-- 注入缓存管理器 -->
        <property name="cacheManager" ref="cacheManager" />
</bean>
```
上面定义了 Shiro 最重要的组件 SecurityManager，并在其中，我们为其配置了一个自定义的 Realm，用于完成用户验证和授权，sessionManager 用于管理 session，
cacheManager 用于管理缓存，为了简单描述，我们先使用默认的 sessionManager 和 cacheManager
```xml
<!-- 缓存管理器 -->
<bean id="cacheManager" class="org.apache.shiro.cache.ehcache.EhCacheManager">
    <property name="cacheManagerConfigFile" value="classpath:cache/ehcache.xml" />
</bean>

<!-- 会话管理器 -->
<bean id="sessionManager"
      class="org.apache.shiro.web.session.mgt.DefaultWebSessionManager">
    <!-- session的失效时长，单位毫秒 -->
    <property name="globalSessionTimeout" value="600000" />
    <!-- 删除失效的session -->
    <property name="deleteInvalidSessions" value="true" />
</bean>
```
还有一些其他的设置，例如 lifecycle 和 AOP式权限检查的配置不再赘述，可以在 resources 目录下的
application-shiro.xml 中看
### shiro filter
现在的一个问题是，如何让系统知道我们在用 Shiro 管理权限呢。这久需要在 web.xml 中先设置好过滤器，
告诉系统所有的请求都需要先经过 shiro 框架过滤。
```xml
<filter>
    <filter-name>shiroFilter</filter-name>
    <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
    <init-param>
        <param-name>targetFilterLifecycle</param-name>
        <param-value>true</param-value>
    </init-param>
</filter>
<filter-mapping>
    <filter-name>shiroFilter</filter-name>
    <url-pattern>/*</url-pattern>
</filter-mapping>
```
DelegatingFilterProxy 作用是自动到 spring 容器查找名字为 shiroFilter（filter-name）的 bean 并把所有 Filter 的操作委托给它。然后将 ShiroFilter 配置到 spring 容器即可

接下来便是在 application-shiro.xml 中定义 shiroFilter：
```xml
<bean id="shiroFilter" class="org.apache.shiro.spring.web.ShiroFilterFactoryBean">
    <property name="securityManager" ref="securityManager" /><!--
    <!-- 配置登陆页面 -->
    <property name="loginUrl" value="${adminPath}/login" />
    <!-- 登陆成功后的一面 -->
    <property name="successUrl" value="${adminPath}?login" />
    <property name="filters">
        <map>
            <!-- 定义过滤器，对应 shiroFilterChainDefinitions 中不同路径所需的过滤器 -->
            <!-- 将自定义 的FormAuthenticationFilter注入shiroFilter中 -->
            <!-- <entry key="cas" value-ref="casFilter"/> -->
            <entry key="authc" value-ref="formAuthenticationFilter"/>
        </map>
    </property>
    <property name="filterChainDefinitions">
        <ref bean="shiroFilterChainDefinitions"/>
    </property>
</bean>

<!-- Shiro权限过滤过滤器定义 -->
<!-- 过虑器链定义，从上向下顺序执行，一般将/**放在最下边 -->
<bean name="shiroFilterChainDefinitions" class="java.lang.String">
    <constructor-arg>
        <value>
            <!-- 静态资源放行 -->
            /static/** = anon            <!-- anon 过滤 -->
            /userfiles/** = anon
            <!--   ${adminPath}/cas = cas        cas 过滤 -->
            ${adminPath}/login = authc   <!-- authc 过滤 -->
            ${adminPath}/logout = logout <!-- logout 过滤 -->
            ${adminPath}/** = user       <!-- user 过滤 -->
            /act/editor/** = user
            /ReportServer/** = user
        </value>
    </constructor-arg>
</bean>
```
此处使用 ShiroFilterFactoryBean 来创建 ShiroFilter 过滤器；filters 属性用于定义自己的
过滤器；例如上面的 `<entry key="authc" value-ref="formAuthenticationFilter"/>`
意思就是 authc 过滤器使用的是自定义的 formAuthenticationFilter.

filterChainDefinitions 用于声明 url 和 filter 的关系，例如：
```text
/static/** = anon
/a/login = authc   
/a/logout = logout 
/a/** = user       
```
表示 /a/login 路径的请求要经过 authc 过滤器，/a/** 使用的是 user 过滤器, /a/static/** 使用匿名过滤器
下表展示一下主要的过滤器：

| 默认拦截器名 | 拦截器类 | 说明 |
| - | - | - |
|authc|	org.apache.shiro.web.filter.authc.FormAuthenticationFilter|基于表单的拦截器；如 “`/**=authc`”，如果没有登录会跳到相应的登录页面登录；主要属性：usernameParam：表单提交的用户名参数名（ username）；  passwordParam：表单提交的密码参数名（password）； rememberMeParam：表单提交的密码参数名（rememberMe）；  loginUrl：登录页面地址（/login.jsp）；successUrl：登录成功后的默认重定向地址； failureKeyAttribute：登录失败后错误信息存储 key（shiroLoginFailure）|
|logout|org.apache.shiro.web.filter.authc.LogoutFilter|退出拦截器，主要属性：redirectUrl：退出成功后重定向的地址（/）; 示例 “/logout=logout”|
|user|org.apache.shiro.web.filter.authc.UserFilter|用户拦截器，用户已经身份验证 / 记住我登录的都可；示例 “/**=user|
|anon|org.apache.shiro.web.filter.authc.AnonymousFilter|匿名拦截器，即不需要登录即可访问；一般用于静态资源过滤；示例 “/static/**=anon”|

## 后台
大概搞清楚了配置之后，我们就要在后台支持用户的验证

接下来就讲解 Shiro 如何在系统的登陆中发挥作用：

### 登陆页
在 spring-mvc.xml 中，我们定义了系统的首页
```xml
<!-- 定义无Controller的path<->view直接映射 -->
<mvc:view-controller path="/" view-name="redirect:${web.view.index}"/>
```
web.view.index 是配置中的一项，值等于 "/a"。就意味着当我们访问系统
http://localhost:8080/devPlat 时候，系统会跳转到 http://localhost:8080/devPlat/a,
但是这个时候系统并不会跳转到主页去，我们看 LoginController 中的代码
```java
    @RequiresPermissions("user")
    @RequestMapping(value = "${adminPath}") // ${adminPath}=/a
    public String index(HttpServletRequest request, HttpServletResponse response){
        if (logger.isDebugEnabled()){
            logger.debug("login success");
        }
        return "index";
    }
```
以及上面的 filterChainDefinitions 都能看出，访问 /a 就必须经过 user 过滤器，如果没有登录的用户
(在 Shiro 中的表现为获取不到 Subject)是不能通过user过滤器的，这时系统会跳转到 loginUrl 参数，
即 "/a/login"，再看 LoginController，我们就知道系统就跳转到了登录界面：
```java
@RequestMapping(value = "${adminPath}/login", method = RequestMethod.GET) // ${adminPath}=/a
public String login(HttpServletRequest request, HttpServletResponse response, Model model){

    return "modules/sys/sysLogin";
}
```
登录界面最关键的也便是一个登录的 form 表单，用于提交验证信息。
```html
	<form id="loginForm" class="form-signin" action="${ctx}/login" method="post">
		<label class="input-label" for="username">登录名</label>
		<input type="text" id="username" name="username" class="input-block-level required" value="${username}">
		<label class="input-label" for="password">密码</label>
		<input type="password" id="password" name="password" class="input-block-level required">
		<input class="btn btn-large btn-primary" type="submit" value="登 录"/>&nbsp;&nbsp;
	</form>
```
由上可知 form 会通过 POST 的方式提交表单到 "/a/login"，看起来对应的应该是 LoginController 的这段：
```java
/**
 * 登录失败，真正登录的POST请求由Filter完成
 */
@RequestMapping(value = "${adminPath}/login", method = RequestMethod.POST)
public String loginFail(HttpServletRequest request, HttpServletResponse response, Model model){
    if(logger.isDebugEnabled()){
        logger.debug("login fail!");
    }
    return "modules/sys/sysLogin";
}
```
但是为什么这里没有任何验证的消息还说登录失败呢。我们想起我们的 filterChainDefinitions，可以看出
"/a/login = authc " 对应的是 authc 过滤器，也就说表单先经过了 authc 过滤器，才到达我们的 LoginController。
这里就涉及 Shiro 的认证过程。

### Shiro认证流程
![shiro4](../pic/shiro4.png)
1. 首先调用 Subject.login(token) 进行登录，其会自动委托给 Security Manager，调用之前必须通过 SecurityUtils.setSecurityManager() 设置；
2. SecurityManager 负责真正的身份验证逻辑；它会委托给 Authenticator 进行身份验证；
3. Authenticator 才是真正的身份验证者，Shiro API 中核心的身份认证入口点，此处可以自定义插入自己的实现；
4. Authenticator 可能会委托给相应的 AuthenticationStrategy 进行多 Realm 身份验证，默认 ModularRealmAuthenticator 会调用 AuthenticationStrategy 进行多 Realm 身份验证；
5. Authenticator 会把相应的 token 传入 Realm，从 Realm 获取身份验证信息，如果没有返回 / 抛出异常表示身份验证失败了。此处可以配置多个 Realm，将按照相应的顺序及策略进行访问。

### 认证
并且在 shiroFilter 中可以知道，我们 authc 过滤器使用的是自定义的 formAuthenticationFilter
```xml
<property name="filters">
    <map>
        <entry key="authc" value-ref="formAuthenticationFilter"/>
    </map>
</property>
```
也就是系统的登陆验证其实是由 formAuthenticationFilter 完成的，看下它的部分代码：
```java
@Service
public class FormAuthenticationFilter extends org.apache.shiro.web.filter.authc.FormAuthenticationFilter {

    public static final String DEFAULT_MESSAGE_PARAM = "message";
    private String messageParam = DEFAULT_MESSAGE_PARAM;

    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) {
        String username = getUsername(request);
        String password = getPassword(request);
        if (password==null){
            password = "";
        }
        boolean rememberMe = isRememberMe(request);
        String host = StringUtils.getRemoteAddr((HttpServletRequest)request);

        return new UsernamePasswordToken(username, password.toCharArray(), rememberMe, host, null);
    }
    
        /**
         * 登陆成功之后跳转的Url
         * @return
         */
    public String getSuccessUrl(){
        return super.getSuccessUrl();
    }

    /**
     * 跳转 Url，不附带参数
     * @param request
     * @param response
     * @throws Exception
     */
    @Override
    protected void issueSuccessRedirect(ServletRequest request, ServletResponse response) throws Exception {
        WebUtils.issueRedirect(request, response, getSuccessUrl(), null, true);
    }

    /**
     * 登陆失败调用界面
     * @param token 认证信息
     * @param e 失败信息
     * @param request
     * @param response
     * @return
     */
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request, ServletResponse response) {
        String className = e.getClass().getName();
        String msg = "";

        // 处理错误信息
        if(IncorrectCredentialsException.class.getName().equals(className)
        || UnknownAccountException.class.getName().equals(className)){
            msg = "用户名或密码错误";
        }
        else if(e.getMessage() != null && StringUtils.startsWith(e.getMessage(), "msg:")){
            msg = StringUtils.replace(e.getMessage(), "msg", "");
        }
        else{
            msg = "系统出现问题，请通知系统管理员。";
            // 输出错误到控制台
            e.printStackTrace();
        }

        request.setAttribute(getFailureKeyAttribute(), className);
        request.setAttribute(getMessageParam(), msg);
        return true;
    }
}
```
结合我们在 Shiro认证流程中说到的，FormAuthenticationFilter 会根据传来的表单中的参数，username，password
等构建一个 `UsernamePasswordToken(username, password.toCharArray(), rememberMe, host, null);`

然后在 FormAuthenticationFilter的父类过滤器执行方法 executeLogin调用 createToken.
```java
protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
    AuthenticationToken token = createToken(request, response);
    if (token == null) {
        String msg = "createToken method implementation returned null. A valid non-null AuthenticationToken " +
                "must be created in order to execute a login attempt.";
        throw new IllegalStateException(msg);
    }
    try {
        Subject subject = getSubject(request, response);
        subject.login(token);
        return onLoginSuccess(token, subject, request, response);
    } catch (AuthenticationException e) {
        return onLoginFailure(token, e, request, response);
    }
}
```

从流程中可知，subject.login(token) 会让 Authenticator 会把相应的 token 传入 Realm，从 Realm 获取身份验证信息
从配置文件中我们知道，我们在配置 securityManager 时候自定义了 Realm 为 systemAuthorizingRealm。

token 会被提交到 systemAuthorizingRealm 中的 doGetAuthenticationInfo(token) 进行验证
```java
    /**
     * 认证回调函数，通过传入的AuthenticationToken进行登录尝试
     * @param token
     * @return
     * @throws AuthenticationException
     */
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        // 转为自定义的Token
        UsernamePasswordToken authToken = (UsernamePasswordToken) token;

        if(logger.isDebugEnabled())
            logger.debug("login submit!");

        // 校验用户名和密码
        String userCode = (String)authToken.getPrincipal();
        // 模拟密码
        String password = "111111";

        // 认证过程
        return new SimpleAuthenticationInfo(userCode, password, this.getName());
    }
```
这里就是验证的最终落地点，为了简便，我们模拟 111111 为数据库取到的用户密码，最后构建一个 AuthenticationInfo：`SimpleAuthenticationInfo(userCode, password, this.getName())`
这里的第一个参数是用户名可以是用户实体，第二个是我们从数据库获取的密码。

AuthenticationInfo 会根据传入的用户，用户密码，来和 token 中的用户提交的用户名密码进行比较，最后返回
是否认证成功。这就是大体的一个 Shiro 认证流程。

### 登陆返回
如果认证成功，认证的主要入口 FormAuthenticationFilter 会通过 issueSuccessRedirect 方法跳转到设置的
successUrl，即 /a?login，最后进入到 LoginController 中, Controller 返回主页，登陆成功。

如果认证失败，FormAuthenticationFilter 会通过 onLoginFailure() 添加失败信息后继续交给 Controller，
也就是进入到了 /a/login，就对应的Controller中的 
```java
@RequestMapping(value = "${adminPath}/login", method = RequestMethod.POST)
public String loginFail(HttpServletRequest request, HttpServletResponse response, Model model){
{// 上面有代码}
```
说明登陆失败，这也是为什么函数名称会起做 loginFail 的原因，该方法返回登陆页面，系统就又进入登陆页面并
显示错误信息。

# 参考资料
- [Shiro](http://shiro.apache.org/authentication.html#apache-shiro-authentication)
- [Shiro 身份验证](https://www.w3cschool.cn/shiro/xgj31if4.html)
- [Shiro-Spring整合](https://www.cnblogs.com/qlqwjy/p/7257502.html)





















































































