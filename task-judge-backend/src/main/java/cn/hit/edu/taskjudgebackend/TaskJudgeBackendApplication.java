package cn.hit.edu.taskjudgebackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("cn.hit.edu.taskjudgebackend.mapper")
@SpringBootApplication
public class TaskJudgeBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskJudgeBackendApplication.class, args);
    }

}
