package club.licona.java;

import com.alibaba.fastjson.JSONArray;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws SQLException {
        // 读取MySQL表mn_buy_ticket中的数据
        String url = "jdbc:mysql://localhost:3306/realtime_computing_kafka?useUnicode=true&characterEncoding=UTF-8";
        Properties properties = new Properties();
        properties.setProperty("driverClassName", "com.mysql.jdbc.Driver");
        properties.setProperty("user", "root");
        properties.setProperty("password", "licona-erp-mysql-password");
        Connection connection = MysqlUtil.createConnection(url, properties.getProperty("driverClassName"), properties.getProperty("user"), properties.getProperty("password"));
        List<Map<String, Object>> maps = MysqlUtil.executeQuery(connection, "select * from mn_monitoring");
        System.out.println("共读取到" + maps.size() + "条数据");

        // 将数据写入到kafka中
        String topic = "liyuming_monitoring_2";

        String bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037";
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties kafkaProperties = new Properties();
        kafkaProperties.put("bootstrap.servers", bootstrapServers);
        kafkaProperties.put("acks", "all");
        kafkaProperties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProperties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        KafkaProducer kafkaProducer = new KafkaProducer(kafkaProperties);

        //      add("liyuming_buy_ticket_2")
        //      add("liyuming_hotel_stay_2")
        //      add("liyuming_monitoring_2")
        maps.forEach(stringObjectMap -> {
            System.out.println(process(stringObjectMap));
            ProducerRecord producerRecord = new ProducerRecord(topic, null, process(stringObjectMap));
            kafkaProducer.send(producerRecord);
        });
        kafkaProducer.flush();
        kafkaProducer.close();
    }

    public static String process(Map<String, Object> stringObjectMap) {
        JSONArray jsonArray = new JSONArray();
        jsonArray.add(stringObjectMap);
        return jsonArray.toString().replace("[", "").replace("]", "");
    }
}
