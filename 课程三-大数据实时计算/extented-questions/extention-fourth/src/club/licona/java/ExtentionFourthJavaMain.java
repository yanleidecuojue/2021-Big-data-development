package club.licona.java;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

public class ExtentionFourthJavaMain {
    public static void main(String[] args) throws Exception {
        /**
         * 输入的主题名称
         */
        String inputTopic = "liyuming";
        /**
         * kafka地址
         */
        String bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037";
        StreamExecutionEnvironment executionEnvironment = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties kafkaProperties = new Properties();
        kafkaProperties.put("bootstrap.servers", bootstrapServers);
        kafkaProperties.put("group.id", UUID.randomUUID().toString());
        kafkaProperties.put("auto.offset.reset", "earliest");
        kafkaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        kafkaProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        FlinkKafkaConsumer010<String> stringFlinkKafkaConsumer010 = new FlinkKafkaConsumer010<>(inputTopic, new SimpleStringSchema(), kafkaProperties);

        stringFlinkKafkaConsumer010.setCommitOffsetsOnCheckpoints(true);
        DataStreamSource<String> stringDataStreamSource = executionEnvironment.addSource(stringFlinkKafkaConsumer010);


        //步骤三：对数据进行处理
        DataStream<WordCount> wordCountSingleOutputStreamOperator = stringDataStreamSource.flatMap(new FlatMapFunction<String, WordCount>() {
            public void flatMap(String line, Collector<WordCount> collector) throws Exception {
                String destination = null;
                if(line.contains("\"")) {
                    destination = line.split(",")[3].split(":")[1].replace("\"", "");
                } else if(line.contains(",")) {
                    destination = line.split(",")[3].split("=")[1];
                }
                collector.collect(new WordCount(destination, 1L));
            }
        });

        // 通过ProcessAllWindowFunction来计算topN
        SingleOutputStreamOperator<WordCount> topN = wordCountSingleOutputStreamOperator
                .keyBy("destination")
                .windowAll(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                .process(new ProcessAllWindowFunction<WordCount, WordCount, TimeWindow>() {
                    @Override
                    public void process(Context context, Iterable<WordCount> iterable, Collector<WordCount> collector) throws SQLException {
                        // 合并word相等的WordCount对象
                        List<WordCount> list = new ArrayList<>();
                        for (WordCount wordCount : iterable) {
                            for (int i = 0; i < list.size(); i++) {
                                if((list.get(i).destination).equals(wordCount.destination)) {
                                    list.set(i, new WordCount(list.get(i).destination, list.get(i).count + 1));
                                }
                            }
                            if(!list.contains(wordCount)) {
                                list.add(wordCount);
                            }
                        }

                        // 对结果进行排序
                        list.sort((o1, o2) -> (int) (o2.count - o1.count));



                        System.out.println("处理完成, 总共得到" + list.size() + "条记录");
                        System.out.println("乘客到达数前五的城市为以下五个: ");

                        /**
                         * MySQL Connection
                         */
                        String url = "jdbc:mysql://localhost:3306/realtime_computing_kafka?useUnicode=true&characterEncoding=UTF-8";
                        Properties properties = new Properties();
                        properties.setProperty("driverClassName", "com.mysql.jdbc.Driver");
                        properties.setProperty("user", "root");
                        properties.setProperty("password", "licona-erp-mysql-password");
                        Connection connection = MysqlUtil.createConnection(url, properties.getProperty("driverClassName"), properties.getProperty("user"), properties.getProperty("password"));

                        // 之后我们将结果数据存储到MySQL中的result表中
                        MysqlUtil.execute(connection, "delete from result");
                        for (int i = 0; i < 5; i++) {
                            System.out.println(list.get(i));
                            String sql = "insert into result(destination,count) values('" + list.get(i).destination + "'," + list.get(i).count + ")";
                            System.out.println(sql);
                            MysqlUtil.execute(connection, sql);
                            collector.collect(list.get(i));
                        }
                    }
                });


        // 任务启动
        executionEnvironment.execute();
    }


    public static class WordCount {
        public String destination;
        public long count;

        //记得要有这个空构建
        public WordCount() {

        }

        public WordCount(String destination, long count) {
            this.destination = destination;
            this.count = count;
        }

        @Override
        public String toString() {
            return "WordCount{" +
                    "destination='" + destination + '\'' +
                    ", count=" + count +
                    '}';
        }

        @Override
        public boolean equals(Object obj) {
            return this.destination.equals(((WordCount) obj).destination);
        }
    }

}



