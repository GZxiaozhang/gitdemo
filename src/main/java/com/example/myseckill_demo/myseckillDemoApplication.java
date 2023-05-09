package com.example.myseckill_demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.example.mySeckill_demo.mapper")
public class myseckillDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(myseckillDemoApplication.class, args);
    }


}
