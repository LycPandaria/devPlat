package edu.devplat.sys.dao;

import edu.devplat.common.persistence.CrudDao;
import edu.devplat.sys.model.Menu;

import java.util.List;

public interface MenuDao extends CrudDao<Menu> {

    List<Menu> findByUserId(String userId);
}
