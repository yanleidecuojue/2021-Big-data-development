package licona.club.sparkdesktopserver.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EditConnectionController {
    @RequestMapping("/edit-connection")
    public ModelAndView mv(){
        ModelAndView modelAndView = new ModelAndView("edit-connection");
        return modelAndView;
    }
}
