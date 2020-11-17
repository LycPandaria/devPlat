package edu.devplat.sys.utils;

import edu.devplat.common.utils.CacheUtils;
import edu.devplat.common.utils.SpringContextHolder;
import edu.devplat.sys.dao.MenuDao;
import edu.devplat.sys.dao.RoleDao;
import edu.devplat.sys.dao.UserDao;
import edu.devplat.sys.model.Menu;
import edu.devplat.sys.model.Role;
import edu.devplat.sys.model.User;
import edu.devplat.sys.security.SystemAuthorizingRealm.Principal;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import java.util.List;


public class UserUtils {
    private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);
    private static MenuDao menuDao = SpringContextHolder.getBean(MenuDao.class);
    private static RoleDao roleDao = SpringContextHolder.getBean(RoleDao.class);

    // Cache Name
    private static final String USER_CACHE = "userCache";
    private static final String USER_CACHE_ID_PREFIX = "id_";
    private static final String USER_CACHE_LOGIN_NAME_PREFIX = "ln_";

    private static final String CACHE_ROLE_LIST = "roleList";
    private static final String CACHE_MENU_LIST = "menuList";

    private static final String USER_CACHE_LIST_BY_OFFICE_ID_PREFIX = "oid_";

    /**
     * 根据ID获取用户
     * @param id
     * @return 取不到返回null
     */
    public static User get(String id){
        // get user from cache
        User user = (User)CacheUtils.get(USER_CACHE, USER_CACHE_ID_PREFIX + id);
        if(user == null){
            user = userDao.get(id);
            if(user == null)    return null;

            // set user's role
            user.setRoleList(roleDao.findList(new Role(user)));
            CacheUtils.put(USER_CACHE, USER_CACHE_ID_PREFIX + user.getId(), user);
            CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_PREFIX + user.getLoginName(), user);
        }

        return user;
    }

    /**
     * 根据登录名获取用户
     * @param loginName
     * @return 取不到返回null
     */
    public static User getByLoginName(String loginName){
        // get user from cache
        User user = (User)CacheUtils.get(USER_CACHE, USER_CACHE_LOGIN_NAME_PREFIX + loginName);
        if(user == null){
            user = userDao.getByLoginName(loginName);
            if(user == null)    return null;

            // set user's role
            user.setRoleList(roleDao.findList(new Role(user)));
            CacheUtils.put(USER_CACHE, USER_CACHE_ID_PREFIX + user.getId(), user);
            CacheUtils.put(USER_CACHE, USER_CACHE_LOGIN_NAME_PREFIX + user.getLoginName(), user);
        }

        return user;
    }

    /**
     * 清除当前用户缓存
     */
    public static void clearCache() {
        // TODO
        removeCache(CACHE_MENU_LIST);
        removeCache(CACHE_ROLE_LIST);

        clearCache(getUser());
    }

    /**
     * 清除指定用户缓存
     * @param user
     */
    public static void clearCache(User user){
        CacheUtils.remove(USER_CACHE, USER_CACHE_ID_PREFIX + user.getId());
        CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_PREFIX + user.getLoginName());
        CacheUtils.remove(USER_CACHE, USER_CACHE_LOGIN_NAME_PREFIX + user.getOldLoginName());

        // 清理 office cache
        if(user.getOffice() != null && user.getOffice().getId() != null)
            CacheUtils.remove(USER_CACHE, USER_CACHE_LIST_BY_OFFICE_ID_PREFIX + user.getOffice().getId());
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

    /**
     * 获得当前用户角色列表
     * @return
     */
    public static List<Role> getRoleList(){
        @SuppressWarnings("unchecked")
        List<Role> roleList =  (List<Role>) getCache(CACHE_ROLE_LIST);
        if(roleList == null){
            User user = getUser();
            if(user.isAdmin()){
                roleList = roleDao.findAllList(new Role());
            }else {
                // 获取用户角色
                roleList = roleDao.findList(new Role(user));
            }
            putCache(CACHE_ROLE_LIST, roleList);
        }
        return roleList;
    }

    /**
     * 获取当前用户授权菜单
     * @return
     */
    public static List<Menu> getMenuList(){
        List<Menu> menuList = (List<Menu>)getCache(CACHE_MENU_LIST);
        if(menuList == null){
            User user = getUser();  //   获取当前用户
            if(user.isAdmin())
                menuList = menuDao.findAllList(new Menu());
            else
                menuList = menuDao.findByUserId(user.getId());

            putCache(CACHE_MENU_LIST, menuList);
        }
        return menuList;
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

    /**
     * 获取授权主要对象
     */
    public static Subject getSubject(){
        return SecurityUtils.getSubject();
    }

    /**
     * 获取 Session
     * @return
     */
    public static Session getSession(){
        try{
            Subject subject = SecurityUtils.getSubject();
            Session session = subject.getSession(false);
            if(session == null)
                session = subject.getSession();
            return session;
        }catch (InvalidSessionException e){
            e.printStackTrace();
        }
        return null;
    }

    // =============================== User Cache==================
    // 这些 cache 是从 Session 中取的，因为是只跟当前的会话有关

    /**
     * 从 session 中获取对应 key 的值
     * @param key
     * @return
     */
    public static Object getCache(String key){
        return getCahce(key, null);
    }

    public static Object getCahce(String key, Object defaultValue){
        Object obj = getSession().getAttribute(key);
        return obj == null ? defaultValue : obj;
    }

    /**
     * set value to session
     * @param key
     * @param value
     */
    public static void putCache(String key, Object value){
        getSession().setAttribute(key, value);
    }

    /**
     * remove key-value attribute from session
     * @param key
     */
    public static void removeCache(String key){
        getSession().removeAttribute(key);
    }
}
