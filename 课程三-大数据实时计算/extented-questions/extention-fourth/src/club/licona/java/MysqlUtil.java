package club.licona.java;

import java.sql.*;
import java.util.*;

public class MysqlUtil {
    public static Connection createConnection(String url, String driverClassName, String user, String password) throws SQLException {
        Properties properties = new Properties();
        properties.setProperty("driverClassName", driverClassName);
        properties.setProperty("user", user);
        properties.setProperty("password", password);
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
        return preparedstatement.execute();
    }


    public static void main(String[] args) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/realtime_computing_kafka?useUnicode=true&characterEncoding=UTF-8";
        Properties properties = new Properties();
        properties.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        properties.setProperty("user", "root");
        properties.setProperty("password", "licona-erp-mysql-password");
        Connection connection = createConnection(url, properties.getProperty("driverClassName"), properties.getProperty("user"), properties.getProperty("password"));
        System.out.println(executeQuery(connection, "select * from mn_buy_ticket"));
    }
}
