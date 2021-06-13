package licona.club.sparkdesktopserver.api;

import com.alibaba.fastjson.JSONObject;
import licona.club.sparkdesktopserver.annotation.UserLoginToken;
import licona.club.sparkdesktopserver.entity.User;
import licona.club.sparkdesktopserver.service.UserService;
import licona.club.sparkdesktopserver.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author jinbin
 * @date 2018-07-08 20:45
 */
@RestController
@RequestMapping("api")
public class UserApi {
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public Object login(@RequestParam("username") String username, @RequestParam("userpassword") String userPassword){
        JSONObject jsonObject=new JSONObject();
        User userForBase=userService.findUserByUsername(username);
        if(userForBase==null){
            jsonObject.put("code", 450);
            jsonObject.put("message","登录失败,用户不存在");
            return jsonObject;
        }else {
            if (!userForBase.getPassword().equals(userPassword)){
                jsonObject.put("code", 451);
                jsonObject.put("message","登录失败,密码错误");
                return jsonObject;
            }else {
                String token = TokenUtil.getToken(userForBase);
                jsonObject.put("code", 200);
                jsonObject.put("msg", "登录成功");
                jsonObject.put("token", token);
                return jsonObject;
            }
        }
    }
    
    @UserLoginToken
    @GetMapping("/getMessage")
    public String getMessage(){
        return "你已通过验证";
    }
}
