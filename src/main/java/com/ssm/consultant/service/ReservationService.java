package com.ssm.consultant.service;

import com.ssm.consultant.mapper.ReservationMapper;
import com.ssm.consultant.pojo.Reservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {
    @Autowired
    private ReservationMapper reservationMapper;
    // 1.添加预约信息的方法
    public void insert(Reservation reservation){
        reservationMapper.insert(reservation);
    }
    // 2.通过手机号查询的方法
    public Reservation findByPhone(String phone){
        return reservationMapper.findByPhone(phone);
    }
}
