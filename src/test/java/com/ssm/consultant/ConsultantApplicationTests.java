package com.ssm.consultant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("需要本地 MySQL、Redis 和模型 API Key，配置完整后可手动开启")
class ConsultantApplicationTests {

    @Test
    void contextLoads() {
    }

}
