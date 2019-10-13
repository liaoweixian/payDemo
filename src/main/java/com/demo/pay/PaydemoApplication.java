package com.demo.pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PaydemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaydemoApplication.class, args);
    }

}

