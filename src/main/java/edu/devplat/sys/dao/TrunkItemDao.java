package edu.devplat.sys.dao;

import edu.devplat.common.persistence.annotation.MyBatisDao;
import edu.devplat.sys.model.TrunkItem;
import org.apache.ibatis.annotations.Param;

@MyBatisDao
public interface TrunkItemDao {
	// �����Ҫ�� mapper.xml ��ʹ�ò���������Ҫ�ڴ���������Ȼֻ��ֻ��#{param1},#{0}����...
	public TrunkItem selectItemByCode(
			@Param("trunkType") Integer trunkType, 
			@Param("itemCode") String itemCode);
}
