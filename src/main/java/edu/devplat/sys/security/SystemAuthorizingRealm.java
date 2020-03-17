package edu.devplat.sys.security;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * 认证安全类实现
 */
@Service
public class SystemAuthorizingRealm extends AuthorizingRealm {

    private Logger logger = LoggerFactory.getLogger(getClass());



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

    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }



//    /**
//     * 用户信息
//     */
//    public static class Principal implements Serializable {
//
//        private static final long serialVersionUID = 1L;
//
//        private String id; // 编号
//        private String loginName; // 登录名
//        private String name; // 姓名
//
//
////		private Map<String, Object> cacheMap;
//
//        public Principal(User user, boolean mobileLogin) {
//            this.id = user.getId();
//            this.loginName = user.getLoginName();
//            this.name = user.getName();
//            this.mobileLogin = mobileLogin;
//        }
//
//        public String getId() {
//            return id;
//        }
//
//        public String getLoginName() {
//            return loginName;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public boolean isMobileLogin() {
//            return mobileLogin;
//        }
//
////		@JsonIgnore
////		public Map<String, Object> getCacheMap() {
////			if (cacheMap==null){
////				cacheMap = new HashMap<String, Object>();
////			}
////			return cacheMap;
////		}
//
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
//
//        @Override
//        public String toString() {
//            return id;
//        }
//
//    }
}
