package edu.devplat.sys.web;

import edu.devplat.common.utils.PasswordUtils;
import edu.devplat.common.utils.StringUtils;
import edu.devplat.common.web.BaseController;
import edu.devplat.sys.model.User;
import edu.devplat.sys.service.SystemService;
import edu.devplat.sys.utils.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping(value = "${adminPath}/sys/user")
public class UserController extends BaseController {

    @Autowired
    private SystemService systemService;

    @ModelAttribute
    public User get(@RequestParam(required = false) String id){
        logger.debug("modelAttribute-get");
        if(StringUtils.isNotBlank(id)){
            return systemService.getUser(id);
        }else{
            return new User();
        }
    }

    @RequestMapping(value = {"index"})
    public String index(User user, Model model) {

        return "modules/sys/userIndex";
    }

    /**
     * 用户信息页面
     * @param user
     * @param response
     * @param model
     * @return
     */
    @RequestMapping(value = "info")
    public String info(User user, HttpServletResponse response, Model model){
        // 传入的 user 是表单中的 user，并不是现在登陆的用户
        User currentUser = UserUtils.getUser();
        if (StringUtils.isNotBlank(user.getName())){
            currentUser.setEmail(user.getEmail());
            currentUser.setPhone(user.getPhone());
            currentUser.setMobile(user.getMobile());
            currentUser.setRemarks(user.getRemarks());
            currentUser.setPhoto(user.getPhoto());
            //systemService.updateUserInfo(currentUser);
            model.addAttribute("message", "保存用户信息成功");
        }
        // 在第一次进入页面，页面上的 user 信息是直接用的 currentUser 的信息
        model.addAttribute("user", currentUser);

        return "modules/sys/userInfo";
    }

    /**
     * 修改密码页面,修改之后需要重新登陆
     * @param oldPassword
     * @param newPassword
     * @param model
     * @return
     */
    @RequestMapping(value = "modifyPwd")
    public String modifyPwd(String oldPassword, String newPassword, Model model){
        User user = UserUtils.getUser();
        // 有参数的话才进入修改密码逻辑，否则只是呈现页面
        if(StringUtils.isNotBlank(oldPassword) && StringUtils.isNotBlank(newPassword)){
            if(PasswordUtils.validatePassword(oldPassword, user.getPassword())){
                // update password
                systemService.updatePasswordById(user.getId(), user.getLoginName(), newPassword);
                model.addAttribute("message", "修改密码成功，重新登陆！");
                UserUtils.getSubject().logout();
                return "modules/sys/sysLogin";
            }else {
                model.addAttribute("message", "修改密码失败，旧密码错误");
            }
        }
        return "modules/sys/userModifyPwd";
    }

}
