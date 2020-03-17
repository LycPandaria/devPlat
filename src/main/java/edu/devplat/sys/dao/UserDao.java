package edu.devplat.sys.dao;

import edu.devplat.common.persistence.CrudDao;
import edu.devplat.common.persistence.annotation.MyBatisDao;
import edu.devplat.sys.model.User;

import java.util.List;

/**
 * 用户DAO接口
 */
@MyBatisDao
public interface UserDao extends CrudDao<User> {

    /**
     * 根据登录名称查询用户
     * @param loginName
     * @return
     */
    public User getByLoginName(String loginName);

    /**
     * 通过OfficeId获取用户列表，仅返回用户id和name（树查询用户时用）
     * @param user
     * @return
     */
    public List<User> findUserByOfficeId(User user);

    /**
     * 查询全部用户数目
     * @return
     */
    public long findAllCount(User user);

    /**
     * 更新用户密码
     * @param user
     * @return
     */
    public int updatePasswordById(User user);

    /**
     * 更新登录信息，如：登录IP、登录时间
     * @param user
     * @return
     */
    public int updateLoginInfo(User user);

    /**
     * 删除用户角色关联数据
     * @param user
     * @return
     */
    public int deleteUserRole(User user);

    /**
     * 插入用户角色关联数据
     * @param user
     * @return
     */
    public int insertUserRole(User user);

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    public int updateUserInfo(User user);

}