package licona.club.sparkdesktopserver.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import licona.club.sparkdesktopserver.annotation.UserLoginToken;
import licona.club.sparkdesktopserver.utils.SparkUtil;
import org.apache.spark.network.protocol.ResponseMessage;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@RestController
@RequestMapping("/api")
public class JdbcApi {
    @UserLoginToken
    @RequestMapping("test-spark-connection")
    public JSONObject getSparkConnection(@RequestParam("connection-host") String connectionHost, @RequestParam("connection-port") String connectionPort,
                                              @RequestParam("connection-database-name") String connectionDatabaseName, @RequestParam("connection-username") String connectionUsername,
                                              @RequestParam("connection-password") String connectionPassword) {
        String url = "jdbc:hive2://" + connectionHost + ":" + connectionPort + "/" + connectionDatabaseName;
        Properties properties = new Properties();
        properties.setProperty("driverClassName", "org.apache.hive.jdbc.HiveDriver");
        properties.setProperty("user", connectionUsername);
        properties.setProperty("password", connectionPassword);
        JSONObject jsonObject = new JSONObject();
        try {
           SparkUtil.createConnection(url, properties.getProperty("driverClassName"), properties.getProperty("user"), properties.getProperty("password"));
        } catch (SQLException throwables) {
            jsonObject.put("code", 405);
            jsonObject.put("msg", "连接spark失败，请检查连接信息");
            return jsonObject;
        }
        jsonObject.put("code", 200);
        jsonObject.put("msg", "连接成功");
        return jsonObject;
    }

    @UserLoginToken
    @RequestMapping("execute-sql")
    public JSONObject executeSql(@RequestParam("connection-host") String connectionHost, @RequestParam("connection-port") String connectionPort,
                                 @RequestParam("connection-database-name") String connectionDatabaseName, @RequestParam("connection-username") String connectionUsername,
                                 @RequestParam("connection-password") String connectionPassword, @RequestParam("sql") String sql) throws SQLException {
        String url = "jdbc:hive2://" + connectionHost + ":" + connectionPort + "/" + connectionDatabaseName;
        Properties properties = new Properties();
        properties.setProperty("driverClassName", "org.apache.hive.jdbc.HiveDriver");
        properties.setProperty("user", connectionUsername);
        properties.setProperty("password", connectionPassword);
        Connection connection = SparkUtil.createConnection(url, properties.getProperty("driverClassName"), properties.getProperty("user"), properties.getProperty("password"));
        List<Map<String, Object>> queryResult = SparkUtil.executeQuery(connection, sql);
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(queryResult);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 200);
        jsonObject.put("msg", "sql执行成功");
        jsonObject.put("data", jsonArray);
        return jsonObject;
    }

    @UserLoginToken
    @RequestMapping("execute-multi-sql")
    public JSONObject executeMultiSql(@RequestParam("connection-host") String connectionHost, @RequestParam("connection-port") String connectionPort,
                                      @RequestParam("connection-database-name") String connectionDatabaseName, @RequestParam("connection-username") String connectionUsername,
                                      @RequestParam("connection-password") String connectionPassword, @RequestParam("sql") String sql) throws SQLException {
        String url = "jdbc:hive2://" + connectionHost + ":" + connectionPort + "/" + connectionDatabaseName;
        Properties properties = new Properties();
        properties.setProperty("driverClassName", "org.apache.hive.jdbc.HiveDriver");
        properties.setProperty("user", connectionUsername);
        properties.setProperty("password", connectionPassword);
        Connection connection = SparkUtil.createConnection(url, properties.getProperty("driverClassName"), properties.getProperty("user"), properties.getProperty("password"));
        String[] sqls = sql.split(";");
        List result = new ArrayList(10);
        for (int i = 0; i < sqls.length; i++) {
            if (sqls[i].trim().startsWith("select") || sqls[i].trim().startsWith("show") || sqls[i].trim().startsWith("desc")) {
                List<Map<String, Object>> list = SparkUtil.executeQuery(connection, sqls[i]);
                result.add(list);
            } else {
                boolean execute = SparkUtil.execute(connection, sqls[i]);
                System.out.println('2');
                result.add(execute);
            }
        }
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(result);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 200);
        jsonObject.put("msg", "sql执行成功");
        jsonObject.put("data", result);
        return jsonObject;
    }

    @UserLoginToken
    @RequestMapping("get-table-data")
    public JSONObject getTableData(@RequestParam("database-name") String currentDatabaseName, @RequestParam(
            "table-name") String tableName, @RequestParam("page") Integer page, @RequestParam("limit") Integer limit) throws SQLException {
        System.out.println(currentDatabaseName + "---" + tableName);
        String url = "jdbc:hive2://bigdata116.depts.bingosoft.net:22116/user23_db";
        Properties properties = new Properties();
        properties.setProperty("driverClassName", "org.apache.hive.jdbc.HiveDriver");
        properties.setProperty("user", "user23");
        properties.setProperty("password", "pass@bingo23");
        Connection connection = SparkUtil.createConnection(url, properties.getProperty("driverClassName"), properties.getProperty("user"), properties.getProperty("password"));

        List<Map<String, Object>> tableCountList = SparkUtil.executeQuery(connection, "select count(*) from " + tableName);


        List<Map<String, Object>> tableDesc = SparkUtil.executeQuery(connection, "desc lym");
        String sql = "use " + currentDatabaseName + ";" + "select t.* from (select *,( row_number() over (order by " + tableDesc.get(0).get("col_name") + ")) rn from  " + tableName + ") t where t.rn between "
                + (page - 1) * limit + " and " + page * limit;

        String[] sqls = sql.split(";");
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < sqls.length; i++) {
            if (sqls[i].startsWith("select") || sqls[i].startsWith("show") || sqls[i].startsWith("desc")) {
                list = SparkUtil.executeQuery(connection, sqls[i]);
            } else {
                boolean execute = SparkUtil.execute(connection, sqls[i]);
            }
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", 0);
        jsonObject.put("msg", "获取表详情成功");
        jsonObject.put("count", tableCountList.get(0).get("count(1)"));
        jsonObject.put("data", list);
        return jsonObject;
    }
}
