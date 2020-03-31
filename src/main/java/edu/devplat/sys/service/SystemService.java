package edu.devplat.sys.service;

import edu.devplat.common.service.BaseService;
import edu.devplat.sys.dao.MenuDao;
import edu.devplat.sys.dao.UserDao;
import edu.devplat.sys.model.User;
import edu.devplat.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 系统管理，安全相关实体的管理类,包括用户、角色、菜单.
 */

@Service
@Transactional(readOnly = true)
public class SystemService extends BaseService {

    public static final String HASH_ALGORITHM = "SHA-1";
    public static final int HASH_INTERATIONS = 1024;
    public static final int SALT_SIZE = 8;

    @Autowired
    private UserDao userDao;
    @Autowired
    private MenuDao menuDao;

    //-- User Service --//

    /**
     * 获取用户
     * @param id
     * @return
     */
    public User getUser(String id) {
        return UserUtils.get(id);
    }

    /**
     * 根据登录名获取用户
     * @param loginName
     * @return
     */
    public User getUserByLoginName(String loginName) {
        return UserUtils.getByLoginName(loginName);
    }

}
