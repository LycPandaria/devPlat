package edu.devplat.sys.utils;

import edu.devplat.common.utils.SpringContextHolder;
import edu.devplat.sys.dao.MenuDao;
import edu.devplat.sys.dao.UserDao;
import edu.devplat.sys.model.Menu;
import edu.devplat.sys.model.User;
import edu.devplat.sys.security.SystemAuthorizingRealm.Principal;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.subject.Subject;

import java.util.List;


public class UserUtils {
    private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);
    private static MenuDao menuDao = SpringContextHolder.getBean(MenuDao.class);

    /**
     * 根据ID获取用户
     * @param id
     * @return 取不到返回null
     */
    public static User get(String id){
        // TODO cache
        return userDao.get(id);
    }

    /**
     * 根据登录名获取用户
     * @param loginName
     * @return 取不到返回null
     */
    public static User getByLoginName(String loginName){
        // TODO cache
        return userDao.getByLoginName(loginName);
    }

    /**
     * 获取当前用户
     * @return 取不到返回 new User()
     */
    public static User getUser(){
        Principal principal = getPrincipal();
        if(principal != null){
            User user = get(principal.getId());
            if(user != null)
                return user;
        }
        return new User();
    }

    public static List<Menu> getMenuList(){
        // TODO cache
        User user = getUser();  //   获取当前用户
        if(user.isAdmin())
            return menuDao.findAllList(new Menu());
        else
            return menuDao.findByUserId(user.getId());
    }


    /**
     * 获取当前登陆对象
     * @return 登陆对象 SystemAuthorizingRealm.principal
     */
    public static Principal getPrincipal(){
        try{
            Subject subject = SecurityUtils.getSubject();
            Principal principal = (Principal) subject.getPrincipal();
            if(principal != null)
                return principal;
        }catch (UnavailableSecurityManagerException e){

        }catch (InvalidSessionException e){

        }
        return null;
    }
}
