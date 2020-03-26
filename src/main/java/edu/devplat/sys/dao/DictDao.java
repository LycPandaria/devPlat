package edu.devplat.sys.dao;

import edu.devplat.common.persistence.CrudDao;
import edu.devplat.common.persistence.annotation.MyBatisDao;
import edu.devplat.sys.model.Dict;

import java.util.List;

/**
 * 字典接口
 * @author liyc
 * @version  20200326
 */
@MyBatisDao
public interface DictDao extends CrudDao<Dict> {

    // 通过类型找字典
    List<String> findTypeList(Dict dict);

}
