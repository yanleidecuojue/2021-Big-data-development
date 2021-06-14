package licona.club.sparkdesktopserver.api;

import com.alibaba.fastjson.JSONObject;
import licona.club.sparkdesktopserver.entity.User;
import licona.club.sparkdesktopserver.service.UserService;
import licona.club.sparkdesktopserver.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author licona
 */
@RestController
@RequestMapping("/api")
public class UserApi {
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public Object login(@RequestParam("username") String username, @RequestParam("userpassword") String userPassword){
        JSONObject jsonObject=new JSONObject();
        User userForBase=userService.findUserByUsername(username);
        if(userForBase==null){
            jsonObject.put("code", 450);
            jsonObject.put("msg","登录失败,用户不存在");
        }else {
            if (!userForBase.getPassword().equals(userPassword)){
                jsonObject.put("code", 451);
                jsonObject.put("msg","登录失败,密码错误");
            }else {
                String token = TokenUtil.getToken(userForBase);
                jsonObject.put("code", 200);
                jsonObject.put("msg", "登录成功");
                jsonObject.put("token", token);
            }
        }
        return jsonObject;
    }

    @PostMapping("/register")
    public Object register(@RequestParam("username") String username, @RequestParam("userpassword") String userPassword){
        JSONObject jsonObject=new JSONObject();
        User userForBase=userService.findUserByUsername(username);
        if(userForBase!=null){
            jsonObject.put("code", 450);
            jsonObject.put("msg","注册失败, 用户已经存在");
        }else {
            User user = new User();
            user.setUsername(username);
            user.setPassword(userPassword);
            int i = userService.insertUser(user);
            if(i == 1) {
                jsonObject.put("code", 200);
                jsonObject.put("msg", "注册成功");
            } else {
                jsonObject.put("code", 451);
                jsonObject.put("msg", "注册失败, 请稍后重试");
            }
        }
        return jsonObject;
    }
}
