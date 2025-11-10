package com.smartmarket.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableRetry
public class SmartMarketBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartMarketBackendApplication.class, args);
    }
}