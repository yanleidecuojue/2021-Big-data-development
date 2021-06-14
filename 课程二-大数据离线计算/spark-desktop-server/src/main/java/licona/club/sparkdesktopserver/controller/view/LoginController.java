package licona.club.sparkdesktopserver.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {
    @RequestMapping("/login")
    public ModelAndView mv(){
        ModelAndView modelAndView = new ModelAndView("login");
        return modelAndView;
    }
}
