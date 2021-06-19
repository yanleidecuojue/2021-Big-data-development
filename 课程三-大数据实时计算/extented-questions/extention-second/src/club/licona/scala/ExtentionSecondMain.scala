package club.licona.scala

import java.util.{Properties, UUID}
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.function.ProcessAllWindowFunction
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010
import org.apache.flink.util.Collector

import scala.collection.JavaConversions.bufferAsJavaList
import scala.collection.mutable.ListBuffer

object ExtentionSecondMain {
  /**
   * 输入的主题名称
   */
  val inputTopic = "liyuming"
  /**
   * kafka地址
   */
  val bootstrapServers = "bigdata35.depts.bingosoft.net:29035,bigdata36.depts.bingosoft.net:29036,bigdata37.depts.bingosoft.net:29037"

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val kafkaProperties = new Properties()
    kafkaProperties.put("bootstrap.servers", bootstrapServers)
    kafkaProperties.put("group.id", UUID.randomUUID().toString)
    kafkaProperties.put("auto.offset.reset", "earliest")
    kafkaProperties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    kafkaProperties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    val kafkaConsumer = new FlinkKafkaConsumer010[String](inputTopic,
      new SimpleStringSchema, kafkaProperties)
    kafkaConsumer.setCommitOffsetsOnCheckpoints(true)
    val inputKafkaStream = env.addSource(kafkaConsumer)

    val topN = inputKafkaStream.map(w => WordCount(w.split(",")(3).split(":")(1).replace("\"", ""), 1))
      .keyBy("destination")
      // 将当前window中所有的行记录，发送过来ProcessAllWindowFunction函数中去处理(可以排序，可以对相同key进行处理)
      // 缺点，window中数据量大时，就容易内存溢出
      .windowAll(TumblingProcessingTimeWindows.of(Time.seconds(1)))
      .process(new ProcessAllWindowFunction[WordCount, WordCount, TimeWindow] {
        override def process(context: Context, elements: Iterable[WordCount], out: Collector[WordCount]): Unit = {
          val list = new ListBuffer[WordCount];
          for (wordCount <- elements) {
            for (i <- list.indices) {
              if (list.get(i).destination == wordCount.destination) list.set(i, WordCount(list.get(i).destination, list.get(i).count + 1))
            }
            if (!list.contains(wordCount)) list.add(wordCount)
          }

          val sortSet = list.toList.sortWith((a, b) => a.count.compareTo(b.count) > 0)

          System.out.println("处理完成, 总共得到" + list.size + "条记录")
          System.out.println("乘客到达数前五的城市为以下五个: ")

          for (i <- 0 until 5) {
            System.out.println(list.get(i))
          }
          System.out.println("注: 没有使用教程中默认的topic，输入topic的文件保存在此部分代码的根目录中")

          for (wordCount <- sortSet) out.collect(wordCount)
        }
      })

    env.execute()
  }

  case class WordCount(destination: String, count: Long) {
    override def equals(obj: Any): Boolean = this.destination == obj.asInstanceOf[WordCount].destination
  }
}

