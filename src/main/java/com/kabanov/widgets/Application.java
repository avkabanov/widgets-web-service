package com.kabanov.widgets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:spring-config.xml")
public class Application {
    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }
}