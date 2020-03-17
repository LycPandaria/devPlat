package edu.devplat.sys.utils;

import edu.devplat.common.utils.SpringContextHolder;
import edu.devplat.sys.dao.UserDao;
import edu.devplat.sys.model.User;

public class UserUtils {
    private static UserDao userDao = SpringContextHolder.getBean(UserDao.class);

    /**
     * 根据ID获取用户
     * @param id
     * @return 取不到返回null
     */
    public static User get(String id){

        return userDao.get(id);
    }

    /**
     * 根据登录名获取用户
     * @param loginName
     * @return 取不到返回null
     */
    public static User getByLoginName(String loginName){

        return userDao.getByLoginName(loginName);
    }
}
