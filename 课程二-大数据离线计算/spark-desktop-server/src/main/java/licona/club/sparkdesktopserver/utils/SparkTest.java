package licona.club.sparkdesktopserver.utils;

import java.sql.*;
import java.util.Properties;

public class SparkTest
{
    public static void main(String[] args) throws SQLException {
        String url = "jdbc:hive2://bigdata116.depts.bingosoft.net:22116/user23_db";
        Properties properties = new Properties();
        properties.setProperty("driverClassName", "org.apache.hive.jdbc.HiveDriver");
        properties.setProperty("user", "user23");
        properties.setProperty("password", "pass@bingo23");

        Connection connection = DriverManager.getConnection(url, properties);
        Statement statement = connection.createStatement();


        ResultSet resultSet = statement.executeQuery("show tables");
        try {
            while (resultSet.next()) {
                String tableName = resultSet.getString(1);
                //输出所有表名
                System.out.println("tableName：" + tableName);
            }
            resultSet.close();

            //查询t_rk_jbxx表结构
            resultSet = statement.executeQuery("desc formatted t_rk_jbxx");
            while (resultSet.next()) {
                String columnName = resultSet.getString(1);
                String columnType = resultSet.getString(2);
                String comment = resultSet.getString(3);
                //输出表结构
                System.out.println("columnName："+columnName   +"  columnType:"+ columnType  +"   comment:"+comment);
            }
            resultSet.close();

            //查询t_rk_jbxx表数据
            System.out.println("表数据：");
            resultSet = statement.executeQuery("select * from t_rk_jbxx limit 10");
            while (resultSet.next()) {
                String asjbh = resultSet.getString(4);
                String ajmc = resultSet.getString(1);
                String bamjbh = resultSet.getString(8);
                //输出表数据

                System.out.println("$asjbh    ｜$ajmc    ｜$bamjbh");
            }
            resultSet.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }
}
