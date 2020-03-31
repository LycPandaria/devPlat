package edu.devplat.sys.web;

import edu.devplat.common.web.BaseController;
import edu.devplat.sys.service.SystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "${adminPath}/sys/menu")
public class MenuController extends BaseController {

    @Autowired
    private SystemService systemService;

    /**
     * 返回首页左侧导航栏
     * @return 左侧导航栏的 html
     */
    @RequestMapping(value = "tree")
    public String tree(){
        return "modules/sys/menuTree";
    }
}
