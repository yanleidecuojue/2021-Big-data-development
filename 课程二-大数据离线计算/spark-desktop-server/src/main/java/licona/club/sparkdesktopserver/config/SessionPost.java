package licona.club.sparkdesktopserver.config;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class SessionPost {
    @PostMapping("/save-user-data")
    public String saveUserData(HttpServletRequest httpServletRequest, @RequestParam("connection-name") String connectionName,
                               @RequestParam("connection-host") String connectionHost,@RequestParam("connection-port") String connectionPort,
                               @RequestParam("connection-database-name") String connectionDatabaseName,@RequestParam("connection-username") String connectionUsername,
                               @RequestParam("connection-password") String connectionPassword) throws JSONException {
        HttpSession session = httpServletRequest.getSession();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("connection-name", connectionName);
        jsonObject.put("connection-host", connectionHost);
        jsonObject.put("connection-port", connectionPort);
        jsonObject.put("connection-database-name", connectionDatabaseName);
        jsonObject.put("connection-username", connectionUsername);
        jsonObject.put("connection-password", connectionPassword);
        session.setAttribute("connection-info", jsonObject);

        return session.getAttribute("connection-info").toString();
    }
}
