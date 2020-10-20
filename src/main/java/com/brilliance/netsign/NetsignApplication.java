package com.brilliance.netsign;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.brilliance.netsign.dao")
public class NetsignApplication {

    public static void main(String[] args) {
        SpringApplication.run(NetsignApplication.class, args);
    }

}
