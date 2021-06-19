#### 题目

##### 实操1

改一改代码，将最近一分钟出现字符"b"的次数统计一下，例如，最近一分钟，出现了12次

**result:**

```
input:
wwwwwwwww b b b ssss y b s t b
output:
2> (b,5)
```

##### 实操2

###### 题目1

为什么kafka接入的数据，返回字段buy_time并没有先后顺序

@TODO 无序原因

###### 题目2

统计乘客到达数前五的城市

**result:**

```
input:
读取kafka中类似以下格式的文本:
{"buy_time":"2020-03-20 20:34:13.0","buy_address":"广州市南站","origin":"广州市","destination":"西安市","username":"lym","gender":"男"}
并进行相关统计，可以修改java代码中flatmap部分或者scala代码中map部分来适应自定义的数据
output:
处理完成, 总共得到6条记录
乘客到达数前五的城市为以下五个: 
WordCount{word='潮州市', count=10}
WordCount{word='乌鲁木齐市', count=5}
WordCount{word='北京市', count=4}
WordCount{word='广州市', count=4}
WordCount{word='西安市', count=3}
注: 没有使用教程中默认的topic，输入topic的文件保存在此部分代码的根目录中
```

注：以上结果为java代码输出结果，scala代码输出结果与此相同

##### 实操3

###### 题目1

读取MySQL的数据入kafka流

见代码

###### 进阶题(可选)：Kafka Connect 实现MySQL增量自动入流

@TODO具体细则

##### 实操4

将Flink流计算的结果入到MySQL中

计算乘客到达数前五的城市并且将其写入MySQL表中

**result**:

```
input:
liyuming topic中的数据
output:
处理完成, 总共得到79条记录
乘客到达数前五的城市为以下五个: 
WordCount{destination='石家庄市', count=154}
insert into result(destination,count) values('石家庄市',154)
WordCount{destination='邵阳市', count=152}
insert into result(destination,count) values('邵阳市',152)
WordCount{destination='泸州市', count=149}
insert into result(destination,count) values('泸州市',149)
WordCount{destination='乌鲁木齐市', count=146}
insert into result(destination,count) values('乌鲁木齐市',146)
WordCount{destination='合肥市', count=144}
insert into result(destination,count) values('合肥市',144)
```

最终可以在数据库中看到相关记录

##### 实操5

在实操中，我们的匹配关键字是写死在代码里，那么我们如何做到匹配关键字可以实时地输入?

connect

