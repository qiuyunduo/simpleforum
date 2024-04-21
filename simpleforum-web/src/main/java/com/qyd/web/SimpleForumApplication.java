package com.qyd.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author 邱运铎
 * @date 2024-04-08 17:24
 */
@Slf4j
@SpringBootApplication
@EnableScheduling
public class SimpleForumApplication {
    public static void main(String[] args) {
        SpringApplication.run(SimpleForumApplication.class, args);
    }
}
