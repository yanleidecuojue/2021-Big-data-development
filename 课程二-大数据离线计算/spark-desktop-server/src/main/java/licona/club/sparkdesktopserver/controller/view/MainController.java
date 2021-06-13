package licona.club.sparkdesktopserver.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {
    @RequestMapping("/main")
    public ModelAndView mv(){
        ModelAndView mv = new ModelAndView("main");
        return mv;
    }
}