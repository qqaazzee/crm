package com.xxxx.crm;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@MapperScan("com.xxxx.crm.dao")
@EnableScheduling  //启用定时任务
public class Starts {
    public static void main(String[] args) {
        SpringApplication.run(Starts.class);
    }
}
