package simpleserver.test.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import edu.devplat.sys.model.TrunkItem;
import edu.devplat.sys.service.TrunkItemService;

import simpleserver.test.SpringTest;

public class TrunkItemServiceTest extends SpringTest{
	@Autowired
	private TrunkItemService service;
	
	@Test
	public void selectItemByCodeTest() {
		TrunkItem item = service.selectItemByCode(1, "39");
		System.out.println(item.getItemName());
	}
}
