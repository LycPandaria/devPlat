package simpleserver.test;

import edu.devplat.common.utils.CacheUtils;
import edu.devplat.common.utils.JedisUtils;
import edu.devplat.sys.service.SystemService;
import edu.devplat.sys.utils.UserUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@ContextConfiguration(locations = { "classpath*:application*.xml" })

@RunWith(SpringJUnit4ClassRunner.class)  
	
public class SpringTest extends AbstractJUnit4SpringContextTests {

    private static Logger logger = LoggerFactory.getLogger(SpringTest.class);

    @Test
    public void testJedis(){
        //JedisUtils.set("Hello", "world", 0);
        //System.out.println(JedisUtils.get("Hello"));

        //UserUtils.get("1");

        //CacheUtils.put("test", "hello", "world");
        //System.out.println(CacheUtils.get("test", "hello"));
    }

}
