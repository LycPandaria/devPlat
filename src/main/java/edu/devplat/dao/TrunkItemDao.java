package edu.devplat.dao;

import edu.devplat.model.TrunkItem;
import org.apache.ibatis.annotations.Param;

public interface TrunkItemDao {
	// �����Ҫ�� mapper.xml ��ʹ�ò���������Ҫ�ڴ���������Ȼֻ��ֻ��#{param1},#{0}����...
	public TrunkItem selectItemByCode(
			@Param("trunkType") Integer trunkType, 
			@Param("itemCode") String itemCode);
}
