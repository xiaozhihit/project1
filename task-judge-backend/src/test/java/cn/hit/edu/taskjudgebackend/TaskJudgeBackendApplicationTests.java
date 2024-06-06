package cn.hit.edu.taskjudgebackend;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class TaskJudgeBackendApplicationTests {

    @Test
    void contextLoads() {
        val encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("1234"));
    }

}
