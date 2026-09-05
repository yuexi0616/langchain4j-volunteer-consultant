package com.ssm.consultant;

import com.ssm.consultant.mapper.ReservationMapper;
import com.ssm.consultant.pojo.Reservation;
import com.ssm.consultant.service.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Disabled("需要本地 MySQL、Redis 和模型 API Key，配置完整后可手动开启")
// @Transactional // 如果加上这个注解，测试结束后会自动回滚，数据库不会有数据
// @Rollback(false) // 如果用了@Transactional，加这个才会真的提交到数据库
public class ReservationServiceTest {
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationMapper reservationMapper;

    // 测试添加
    @Test
    void testInsert(){
        String phone = "13300000100"; // 用一个不重复的测试手机号

        Reservation reservation = new Reservation(null, "测试小王", "男", phone, LocalDateTime.now(), "上海", 580);

        reservationService.insert(reservation);

    }

    // 测试查询
    @Test
    void testFindByPhone(){
        Reservation found = reservationMapper.findByPhone("13300000100");
        System.out.println(found);
    }
}
