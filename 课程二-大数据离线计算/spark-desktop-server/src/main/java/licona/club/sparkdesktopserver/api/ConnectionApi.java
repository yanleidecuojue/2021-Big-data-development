package licona.club.sparkdesktopserver.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import licona.club.sparkdesktopserver.annotation.UserLoginToken;
import licona.club.sparkdesktopserver.entity.Connection;
import licona.club.sparkdesktopserver.service.ConnectionService;
import licona.club.sparkdesktopserver.utils.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ConnectionApi {
    @Autowired
    ConnectionService connectionService;

    @UserLoginToken
    @RequestMapping("create-connection")
    public JSONObject createConnection(HttpServletRequest httpServletRequest, @RequestParam("connection-name") String connectionName,
                                       @RequestParam("connection-host") String connectionHost, @RequestParam("connection-port") String connectionPort,
                                       @RequestParam("connection-database-name") String connectionDatabaseName, @RequestParam("connection-username") String connectionUsername,
                                       @RequestParam("connection-password") String connectionPassword) {
        String token = httpServletRequest.getHeader("token");
        String userId = TokenUtil.decodeToken(token);
        Connection connection = new Connection();
        connection.setUid(Long.valueOf(userId));
        connection.setConnectionName(connectionName);
        connection.setConnectionHost(connectionHost);
        connection.setConnectionPort(connectionPort);
        connection.setConnectionDatabaseName(connectionDatabaseName);
        connection.setConnectionUsername(connectionUsername);
        connection.setConnectionPassword(connectionPassword);
        int i = connectionService.putConnection(connection);
        JSONObject jsonObject = new JSONObject();
        if (i == 1) {
            jsonObject.put("code", 200);
            jsonObject.put("msg", "创建连接成功");
        } else {
            jsonObject.put("code", 405);
            jsonObject.put("msg", "创建连接失败");
        }
        return jsonObject;
    }

    @UserLoginToken
    @RequestMapping("query-connections")
    public JSONObject getUserConnections(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("token");
        String userId = TokenUtil.decodeToken(token);

        List<Connection> connections = connectionService.queryUserConnections(userId);

        JSONArray jsonArray = new JSONArray();
        jsonArray.add(connections);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 200);
        jsonObject.put("msg", "查询连接成功");
        jsonObject.put("connections", jsonArray);
        return jsonObject;
    }

    @UserLoginToken
    @RequestMapping("update-connection")
    public JSONObject updateConnection(HttpServletRequest httpServletRequest, @RequestParam("connection-name") String connectionName,
                                       @RequestParam("connection-host") String connectionHost, @RequestParam("connection-port") String connectionPort,
                                       @RequestParam("connection-database-name") String connectionDatabaseName, @RequestParam("connection-username") String connectionUsername,
                                       @RequestParam("connection-password") String connectionPassword) {
        String token = httpServletRequest.getHeader("token");
        String userId = TokenUtil.decodeToken(token);

        Connection connection = new Connection();
        connection.setUid(Long.valueOf(userId));
        connection.setConnectionName(connectionName);
        connection.setConnectionHost(connectionHost);
        connection.setConnectionPort(connectionPort);
        connection.setConnectionDatabaseName(connectionDatabaseName);
        connection.setConnectionUsername(connectionUsername);
        connection.setConnectionPassword(connectionPassword);

        int i = connectionService.updateConnection(connection);
        JSONObject jsonObject = new JSONObject();
        if (i == 1) {
            jsonObject.put("code", 200);
            jsonObject.put("msg", "更新连接成功");
        } else {
            jsonObject.put("code", 405);
            jsonObject.put("msg", "更新连接失败");
        }
        return jsonObject;
    }
}
