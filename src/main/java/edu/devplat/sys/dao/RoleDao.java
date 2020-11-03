package edu.devplat.sys.dao;

import edu.devplat.common.persistence.CrudDao;
import edu.devplat.common.persistence.annotation.MyBatisDao;
import edu.devplat.sys.model.Role;

/**
 * 角色 DAO 接口
 */
@MyBatisDao
public interface RoleDao extends CrudDao<Role> {
    Role getByName(Role role);

    Role getByEnname(Role role);

    /**
     * 维护角色与菜单权限关系
     * @param role
     * @return
     */
    int deleteRoleMenu(Role role);

    int insertRoleMenu(Role role);
}
