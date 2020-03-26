package edu.devplat.sys.security;

import edu.devplat.common.config.Global;
import edu.devplat.common.utils.Encodes;
import edu.devplat.common.utils.SpringContextHolder;
import edu.devplat.sys.dao.UserDao;
import edu.devplat.sys.model.User;
import edu.devplat.sys.service.SystemService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;

/**
 * 认证安全类实现
 */
@Service
public class SystemAuthorizingRealm extends AuthorizingRealm {

    private Logger logger = LoggerFactory.getLogger(getClass());


    private SystemService systemService;

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
        // 获取用于
        User user = getSystemService().getUserByLoginName(authToken.getUsername());
        if(user != null){
            if(Global.NO.equals(user.getLoginFlag()))
                throw new AuthenticationException("msg:该已帐号禁止登录.");
            // 密码盐
            byte[] salt = Encodes.decodeHex(user.getPassword().substring(0,16));

            // 认证过程
            return new SimpleAuthenticationInfo(new Principal(user),
                    user.getPassword().substring(16), ByteSource.Util.bytes(salt), getName());
        }else
            return null;

    }

    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }


    /**
     * 获取系统业务对象
     */
    public SystemService getSystemService() {
        if (systemService == null){
            systemService = SpringContextHolder.getBean(SystemService.class);
        }
        return systemService;
    }

    /**
     * 设定密码校验的Hash算法与迭代次数
     */
    @PostConstruct
    public void initCredentialsMatcher() {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher(SystemService.HASH_ALGORITHM);
        matcher.setHashIterations(SystemService.HASH_INTERATIONS);
        setCredentialsMatcher(matcher);
    }

    /**
     * 用户信息,自定义的 Shiro principal，这样可以存储自己想要的信息
     * 作为登陆了的用户的信息
     */
    public static class Principal implements Serializable {

        private static final long serialVersionUID = 1L;

        private String id; // 编号
        private String loginName; // 登录名
        private String name; // 姓名


//		private Map<String, Object> cacheMap;

        public Principal(User user) {
            this.id = user.getId();
            this.loginName = user.getLoginName();
            this.name = user.getName();
        }

        public String getId() {
            return id;
        }

        public String getLoginName() {
            return loginName;
        }

        public String getName() {
            return name;
        }


//		@JsonIgnore
//		public Map<String, Object> getCacheMap() {
//			if (cacheMap==null){
//				cacheMap = new HashMap<String, Object>();
//			}
//			return cacheMap;
//		}

//        /**
//         * 获取SESSIONID
//         */
//        public String getSessionid() {
//            try{
//                return (String) UserUtils.getSession().getId();
//            }catch (Exception e) {
//                return "";
//            }
//        }

        @Override
        public String toString() {
            return id;
        }

    }
}
