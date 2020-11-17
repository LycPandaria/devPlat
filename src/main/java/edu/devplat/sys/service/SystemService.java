package edu.devplat.sys.service;

import edu.devplat.common.security.Digests;
import edu.devplat.common.service.BaseService;
import edu.devplat.common.utils.Encodes;
import edu.devplat.sys.dao.MenuDao;
import edu.devplat.sys.dao.UserDao;
import edu.devplat.sys.model.User;
import edu.devplat.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.devplat.common.utils.PasswordUtils;

/**
 * 系统管理，安全相关实体的管理类,包括用户、角色、菜单.
 */

@Service
@Transactional(readOnly = true)
public class SystemService extends BaseService {

    @Autowired
    private UserDao userDao;
    @Autowired
    private MenuDao menuDao;

    //-- User Service --//

    // TODO

    /**
     * 获取用户
     *
     * @param id
     * @return
     */
    public User getUser(String id) {
        return UserUtils.get(id);
    }

    /**
     * 根据登录名获取用户
     *
     * @param loginName
     * @return
     */
    public User getUserByLoginName(String loginName) {
        return UserUtils.getByLoginName(loginName);
    }

    /**
     * 更新用户密码
     *
     * @param uid
     * @param loginName
     * @param newPassword
     */
    @Transactional(readOnly = false)
    public void updatePasswordById(String uid, String loginName, String newPassword) {
        User user = new User(uid);
        user.setPassword(PasswordUtils.entryptPassword(newPassword));
        userDao.updatePasswordById(user);
        // 清除用户缓存
        user.setLoginName(loginName);
        UserUtils.clearCache(user);
    }

    @Transactional(readOnly = false)
    public void updateUserInfo(User user){
        // 记录 update 时间 和 操作者
        user.preUpdate();
        userDao.updateUserInfo(user);
        // 清除用户缓存
        UserUtils.clearCache(user);
    }


}
