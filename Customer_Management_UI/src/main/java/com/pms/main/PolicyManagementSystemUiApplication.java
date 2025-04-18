package com.pms.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.pms.controller", "com.pms.service"})  // Include the service package
@EntityScan("com.pms.entity")  // Ensure entities are scanned

public class PolicyManagementSystemUiApplication {
    public static void main(String[] args) {
        SpringApplication.run(PolicyManagementSystemUiApplication.class, args);
    }
}
