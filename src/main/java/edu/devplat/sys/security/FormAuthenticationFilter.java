package edu.devplat.sys.security;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import edu.devplat.common.utils.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.stereotype.Service;

/**
 * 表单验证（包含验证码）过滤类
 * @author lyc
 *
 * 这个类用于过滤 auth 对应的网址（application-shiro.xml 中定义过）
 * 如果验证失败，会跳转回 loginUrl（application-shiro.xml 中定义过）
 *
 * 这个过滤器会构建一个 UsernamePasswordToken,里面会包含 username，password，rememberMe 参数
 * 最后会调用 Subject.login(usernamePasswordToken) 来进行登陆尝试
 */
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
        //String captcha = getCaptcha(request);

        return new UsernamePasswordToken(username, password.toCharArray(), rememberMe, host, null);
    }

    /**
     * 获取登陆用户名
     * @param request
     * @return
     */
    @Override
    protected String getUsername(ServletRequest request){
        String username = super.getUsername(request);
        if(StringUtils.isBlank(username))
            username = StringUtils.toString(request.getAttribute(getUsernameParam()), StringUtils.EMPTY);
        return username;
    }

    /**
     * 获取登陆密码
     * @param request
     * @return
     */
    @Override
    protected String getPassword(ServletRequest request){
        String password = super.getPassword(request);
        if(StringUtils.isBlank(password))
            password = StringUtils.toString(request.getAttribute(getPasswordParam()), StringUtils.EMPTY);
        return password;
    }

    /**
     * 获取是否记住我
     * @param request
     * @return
     */
    @Override
    protected boolean isRememberMe(ServletRequest request) {
        String isRememberMe = WebUtils.getCleanParam(request,getRememberMeParam());
        if(StringUtils.isBlank(isRememberMe))
            isRememberMe = StringUtils.toString(request.getAttribute(getRememberMeParam()), StringUtils.EMPTY);
        return StringUtils.toBoolean(isRememberMe);
    }

    /**
     * 登陆成功之后跳转的Url
     * @return
     */
    public String getSuccessUrl(){
        return super.getSuccessUrl();
    }

    public String getMessageParam(){
        return messageParam;
    }

    /**
     * 跳转 Url，不附带参数
     * @param request
     * @param response
     * @throws Exception
     */
    @Override
    protected void issueSuccessRedirect(ServletRequest request, ServletResponse response) throws Exception {
        System.out.println("Password:" + getPassword(request));
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
