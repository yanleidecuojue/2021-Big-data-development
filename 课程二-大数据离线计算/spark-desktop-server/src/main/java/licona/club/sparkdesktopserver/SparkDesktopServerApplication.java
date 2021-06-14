package licona.club.sparkdesktopserver;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(value = "licona.club.sparkdesktopserver.mapper")
public class SparkDesktopServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SparkDesktopServerApplication.class, args);
    }
}
