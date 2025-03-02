package com.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 全链路安全加密查询系统主应用类
 * 实现对身份证号的全链路加密查询
 */
@SpringBootApplication
@EnableScheduling
public class SecureQueryApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecureQueryApplication.class, args);
    }
}