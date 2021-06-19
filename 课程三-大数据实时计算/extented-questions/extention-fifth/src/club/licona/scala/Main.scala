package club.licona.scala

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.api.functions.co.CoProcessFunction
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema
import org.apache.flink.util.Collector

import java.util
import java.util.{Properties, UUID}
import scala.collection.mutable.ListBuffer

object Main {
  //需要监控的人名
  var user = "汤欣欣"
  val inputTopics: util.ArrayList[String] = new util.ArrayList[String]() {
    {
      add("liyuming_buy_ticket_2") //车票购买记录主题
      add("liyuming_hotel_stay_2") //酒店入住信息主题
      add("liyuming_monitoring_2") //监控系统数据主题
    }
  }
  val bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037"

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setParallelism(1)
    val kafkaProperties = new Properties()
    kafkaProperties.put("bootstrap.servers", bootstrapServers)
    kafkaProperties.put("group.id", UUID.randomUUID().toString)
    kafkaProperties.put("auto.offset.reset", "earliest")
    kafkaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    val kafkaConsumer = new FlinkKafkaConsumer010[ObjectNode](inputTopics,
      new JSONKeyValueDeserializationSchema(true), kafkaProperties)
    kafkaConsumer.setCommitOffsetsOnCheckpoints(true)
    val inputKafkaStream = env.addSource(kafkaConsumer)

    val text = env.socketTextStream("localhost", 9999)


    text.connect(inputKafkaStream).process(new CoProcessFunction[String, ObjectNode, String] {
      val list = new ListBuffer[JsonNode]

      override def processElement1(in1: String, context: CoProcessFunction[String, ObjectNode, String]#Context, collector: Collector[String]): Unit = {
        println(list.size)
        if (list.size > 0) {
          val ans = list.filter(x => x.get("value").get("username").asText("").equals(in1)).map(x => {
            (x.get("metadata").get("topic").asText("") match {
              case "mn_monitoring_1"
              => x.get("value").get("found_time")
              case _ => x.get("value").get("buy_time")
            }, x)
          })
          println(ans)
        }
      }

      override def processElement2(in2: ObjectNode, context: CoProcessFunction[String, ObjectNode, String]#Context, collector: Collector[String]): Unit = {
        list.append(in2)
      }
    })
    env.execute()
  }
}