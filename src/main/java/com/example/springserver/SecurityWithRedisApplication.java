package com.example.securitywithredis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SecurityWithRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(SecurityWithRedisApplication.class, args);
    }

}
