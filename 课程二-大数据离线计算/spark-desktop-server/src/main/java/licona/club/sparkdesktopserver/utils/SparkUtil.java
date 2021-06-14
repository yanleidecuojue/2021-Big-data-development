package licona.club.sparkdesktopserver.utils;

import java.sql.*;
import java.util.*;

public class SparkUtil {
    public static Connection createConnection(String url, String driverClassName, String user, String password) throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("driverClassName", "org.apache.hive.jdbc.HiveDriver");
        properties.setProperty("user", "user23");
        properties.setProperty("password", "pass@bingo23");
        return DriverManager.getConnection(url, properties);
    }

    public static List<Map<String, Object>> executeQuery(Connection connection, String sql) {
        PreparedStatement preparedstatement = null;
        ResultSet rs = null;

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        try {
            preparedstatement = (PreparedStatement) connection.prepareStatement(sql);

            rs = preparedstatement.executeQuery();
            // 获取元数据
            ResultSetMetaData rsmd = rs.getMetaData();
            Map<String, Object> mapMetaData = new HashMap<String, Object>();
            // 打印一列的列名
            while (rs.next()) {
                //获取数据表中满足要求的一行数据，并放入Map中
                for (int i = 0; i < rsmd.getColumnCount(); i++) {
                    String columnLabel = rsmd.getColumnLabel(i + 1);
                    Object columnValue = rs.getObject(columnLabel);
                    mapMetaData.put(columnLabel, columnValue);
                }
                Map<String, Object> tmpMap = new HashMap<>();
                //将Map中的数据通过反射初始化T类型对象
                if (mapMetaData.size() > 0) {
                    for (Map.Entry<String, Object> entry : mapMetaData.entrySet()) {
                        tmpMap.put(entry.getKey(), entry.getValue());
                    }
                }
                //将对象装入Vector容器
                list.add(tmpMap);
            }
            rs.close();
            preparedstatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean execute(Connection connection, String sql) throws SQLException {
        PreparedStatement preparedstatement = null;
        preparedstatement = connection.prepareStatement(sql);
        return preparedstatement.execute( );
    }


    public static void main(String[] args) throws SQLException {
//        // 只执行一条语句
//        String url = "jdbc:hive2://bigdata116.depts.bingosoft.net:22116/user23_db";
//        Properties properties = new Properties();
//        properties.setProperty("driverClassName", "org.apache.hive.jdbc.HiveDriver");
//        properties.setProperty("user", "user23");
//        properties.setProperty("password", "pass@bingo23");
//        Connection connection = createConnection(url, properties.getProperty("driverClassName"), properties.getProperty("user"), properties.getProperty("password"));
//        List<Map<String, Object>> queryResult = executeQuery(connection, "use user08_db show tables");
//        System.out.println(queryResult);
        // 同时执行多条语句
        String url = "jdbc:hive2://bigdata116.depts.bingosoft.net:22116/user23_db";
        Properties properties = new Properties();
        properties.setProperty("driverClassName", "org.apache.hive.jdbc.HiveDriver");
        properties.setProperty("user", "user23");
        properties.setProperty("password", "pass@bingo23");
        Connection connection = createConnection(url, properties.getProperty("driverClassName"), properties.getProperty("user"), properties.getProperty("password"));
        System.out.println(execute(connection, "use user23_db"));

//        List<Map<String, Object>> queryResult = executeQuery(connection, "select * from t_rk_jbxx t where id>20 order by id asc limit 10");
        List<Map<String, Object>> maps = executeQuery(connection, "select t.* from (select *,( row_number() over (order by sfzhm)) rn from  t_rk_jbxx) t where t.rn between 5 and 7");
        System.out.println(maps.get(0).get("col_name"));
        List<Map<String, Object>> queryResult = executeQuery(connection, "select count(*) from lym");
        System.out.println(queryResult.get(0).get("count(1)"));
    }
}

