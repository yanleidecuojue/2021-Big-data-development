package licona.club

import org.apache.flink.api.scala.createTypeInformation
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.node.ObjectNode
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.apache.flink.streaming.util.serialization.JSONKeyValueDeserializationSchema

import java.text.{DateFormat, SimpleDateFormat}
import java.util.{Date, Properties, UUID}

object Consumer {
  val accessKey = "9955B832123E755E5E98"
  val secretKey = "WzI3RTA1RDEyRjc1NDRERjU4NDQwNDg4MzVBNTcz"
  val endpoint = "http://10.16.0.1:81"
  val bucket = "liyuming"
  //上传文件的路径前缀
  val pastKeyPrefix = "upload/past/"
  val curKeyPrefix = "upload/cur/"
  //上传数据间隔 单位毫秒
  val period = 5000
  //输入的kafka主题名称
  val inputTopic = "liyuming_buy_ticket_2"
  //kafka地址
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
    val kafkaConsumer = new FlinkKafkaConsumer010[ObjectNode](inputTopic,
      new JSONKeyValueDeserializationSchema(true), kafkaProperties)
    kafkaConsumer.setCommitOffsetsOnCheckpoints(true)
    val inputKafkaStream = env.addSource(kafkaConsumer)
    // 2019-06-20 00:40:16 => 1560962416
    val past = inputKafkaStream.filter(item => {
      TimeCompare(tranTimeToString(item.get("value").get("buy_time").toString), "2019-06-20 00:40:16")
    })
    val cur = inputKafkaStream.filter(item => {
      TimeCompare("2019-06-20 00:40:16", tranTimeToString(item.get("value").get("buy_time").toString))
    })
    past.writeUsingOutputFormat(new S3Writer(accessKey, secretKey, endpoint, bucket, pastKeyPrefix, period))

    cur.writeUsingOutputFormat(new S3Writer(accessKey, secretKey, endpoint, bucket, curKeyPrefix, period))
    env.execute()
  }

  def tranTimeToString(tm: String): String = {
    val fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val tim = fm.format(new Date(tm.toLong))
    tim
  }

  def TimeCompare(buyTime: String, constantTime: String): Boolean = {
    var flag = false
    val df: DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    try {
      val buy: Date = df.parse(buyTime)
      val constant: Date = df.parse(constantTime)

      val bs: Long = buy.getTime - constant.getTime
      if (bs < 0) {
        flag = true
      }
    }
    catch {
      case e: Exception => {
      }
    }
    flag
  }
}
