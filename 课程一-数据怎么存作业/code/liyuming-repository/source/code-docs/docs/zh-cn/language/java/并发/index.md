#### 进程

#### 线程

##### 创建线程

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Test")
public class Test {
    public static void main(String[] args) {
        Thread t = new Thread() {
            @Override
            public void run() {
                log.debug("running");
            }
        };

        t.setName("thread_test");
        t.start();

        log.debug("running");
    }
}
```

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Test")
public class Test {
    public static void main(String[] args) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                log.debug("running");
            }
        };

        Thread t = new Thread(runnable, "thread_test");

        t.start();

        log.debug("running");
    }
}
```

```java
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@Slf4j(topic = "c.Test")
public class Test {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FutureTask futureTask = new FutureTask<>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("running...");
                Thread.sleep(1000);
                return 100;
            }
        });

        Thread t = new Thread(futureTask);
        t.start();

        log.debug("{}", futureTask.get());
    }
}

```

##### lamda创建线程

```java
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.Test")
public class Test {
    public static void main(String[] args) {

        Thread t = new Thread(() -> log.debug("running"), "thread_test");

        t.start();

        log.debug("running");
    }
}
```

##### 查看进程线程方法

```shell
# windows
tasklist
tasklist | findstr java
taskkill
# linux
ps -ef | grep java
jps
kill pid
top -H -p pid 进程中线程信息
jstack pid 进程中线程信息
# jconsole
java -Djava.rmi.server.hostname=`ip地址` -Dcom.sun.management.jmxremote -
Dcom.sun.management.jmxremote.port=`连接端口` -Dcom.sun.management.jmxremote.ssl=是否安全连接 -
Dcom.sun.management.jmxremote.authenticate=是否认证 java类
```



#### 并发



#### 并行