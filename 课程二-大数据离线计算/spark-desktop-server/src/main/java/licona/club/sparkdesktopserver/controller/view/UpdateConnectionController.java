package licona.club.sparkdesktopserver.controller.view;

import licona.club.sparkdesktopserver.entity.Connection;
import licona.club.sparkdesktopserver.service.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UpdateConnectionController {
    @Autowired
    ConnectionService connectionService;

    @GetMapping("/update-connection")
    public ModelAndView mv(@RequestParam("connection-name") String connectionName){
        System.out.println(connectionName);
        ModelAndView modelAndView = new ModelAndView("update-connection");
        Connection connection = connectionService.findConnectionByCname(connectionName);
        modelAndView.addObject("connection", connection);
        return modelAndView;
    }
}
