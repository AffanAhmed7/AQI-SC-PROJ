package com.example.aqisystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AqisystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(AqisystemApplication.class, args);
    }
}
