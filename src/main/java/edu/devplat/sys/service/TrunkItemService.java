package edu.devplat.sys.service;

import edu.devplat.sys.model.TrunkItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.devplat.sys.dao.TrunkItemDao;

@Service
public class TrunkItemService {
	
	@Autowired
	private TrunkItemDao dao;
	
	public TrunkItem selectItemByCode(Integer trunkType, String itemCode) {
		return dao.selectItemByCode(trunkType, itemCode);
	}
}
