之前的文章，我们讲了怎么创建一个 Global 类并怎么调用它。但是如果我们想在 jsp 页面上也调用这个 Global 类怎么办，总不能还要写个
http request 吧。

这个时候我们一般会使用到 jsp:tag 和 jsp:tld。

jsp:tag主要做页面进行逻辑处理后显示，最后的效果就是你可以给T一些参数，T会处理后把产生的结果显示在页面中。
举个栗子：<c:if>,<c:for>,<c:set> 都是这样实现的，不信你ctrl点进去看看呗。

jsp:tld会映射到一个具体的类的方法，最后的效果就是你可以在页面上写个标签就可以把数据库的数据显示到页面中。
举个例子：<sec:authorize>, <shiro:hasRole> 等。

这里我们先不讲 tag，先根据 Global 类来讲 tld 在项目中的作用。

## 创建tld文件
习惯是先在WEB-INF下创建一个tld文件夹,比如：WEB-INF/tlds

再创建 tld，我的命名习惯是 fnXXX。比如：fns.tld   最后的 s 是：System 的缩写。

<center>tlds/fns.tld</center>
```xml
<?xml version="1.0" encoding="UTF-8" ?>

<taglib xmlns="http://java.sun.com/xml/ns/j2ee"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-jsptaglibrary_2_0.xsd"
  version="2.0">
    
  <description>JSTL 1.1 functions library</description>
  <display-name>JSTL functions sys</display-name>
  <tlib-version>1.1</tlib-version>
  <short-name>fns</short-name>
  <uri>http://java.sun.com/jsp/jstl/functionss</uri>

  <function>
    <description>获取管理路径</description>
    <name>getAdminPath</name>
    <function-class>edu.devplat.common.config.Global</function-class>
    <function-signature>java.lang.String getAdminPath()</function-signature>
    <example>${fns:getAdminPath()}</example>
  </function>
  
</taglib>
```
从上图便可以看出，其实 tld 文件是定义了一个 taglib 文件，我们在其中写一个 function，通过 function-class 和 function-signature
可以定义到一个固定的类的一份方法。

当然，并不是在这里只能用 Global 类的方法，页面中用到的很多方法都会通过这个 tld 来调用。
```xml
<function>
    <description>根据编码获取用户对象</description>
    <name>getUserById</name>
    <function-class>edu.devplat.sys.utils.UserUtils</function-class>
    <function-signature>edu.devplat.sys.entity.User get(java.lang.String)</function-signature>
    <example>${fns:getUserById(id)}</example> 
</function>

<function>
    <description>获取字典对象列表</description>
    <name>getDictList</name>
    <function-class>edu.devplat.sys.utils.DictUtils</function-class>
    <function-signature>java.util.List getDictList(java.lang.String)</function-signature>
    <example>${fns:getDictList(type)}</example> 
</function>
```

在 example 中，我们就知道通过在页面调用 `${fns:getAdminPath()}` 便可以访问到这个方法。

## 使用 tld 文件
因为项目中不止一个 tld 文件，同时还有很多 tag 文件，为了方便，我们建了一个 taglib.jsp 里面就包含了所有系统
用到的 taglib 文件

<center>/include/taglib.jsp</center>

```jsp
<%@ taglib prefix="shiro" uri="/WEB-INF/tlds/shiros.tld" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fns" uri="/WEB-INF/tlds/fns.tld" %>
<%@ taglib prefix="fnc" uri="/WEB-INF/tlds/fnc.tld" %>
<%@ taglib prefix="sys" tagdir="/WEB-INF/tags/sys" %>
<%--<%@ taglib prefix="act" tagdir="/WEB-INF/tags/act" %>--%>
<%--<%@ taglib prefix="cms" tagdir="/WEB-INF/tags/cms" %>--%>
<c:set var="ctx" value="${pageContext.request.contextPath}${fns:getAdminPath()}"/>
<c:set var="ctxStatic" value="${pageContext.request.contextPath}/static"/>
```

接下来我们在需要用到的界面中 include 这个 jsp 即可。注意上面代码中的 "ctx" 和 "ctxStatic" 中使用了我们在 fns.tld 中定义的
方法，同时这两个参数也是很多界面中使用，来源就是来源于这个 taglib.jsp 以及相应的 fns.tld 文件。
```jsp
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
```

使用也很简单
```jsp
<div class="footer">
	By DanChaofan ${fns:getConfig('version')}
</div>
```

