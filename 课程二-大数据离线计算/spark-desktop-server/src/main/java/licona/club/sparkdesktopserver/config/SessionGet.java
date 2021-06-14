package licona.club.sparkdesktopserver.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.HttpCookie;

@RestController
public class SessionGet {
    @GetMapping("/get-user-data")
    public String getUserData(HttpServletRequest httpServletRequest) {
        HttpSession session = httpServletRequest.getSession();

        session.getAttribute("connection-info");

        return session.getAttribute("connection-info").toString();
    }
}
