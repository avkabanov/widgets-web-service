package com.kabanov.widgets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kabanov.widgets.interseptor.rate_limit.RateLimitInterceptor;

// TODO fix or remove
/*@EnableConfigServer
@EnableAutoConfiguration*/
@EnableScheduling
@SpringBootApplication
@ImportResource("classpath:spring-config.xml")
public class Application implements WebMvcConfigurer {
    
    @Autowired 
    private RateLimitInterceptor interceptor;
    
    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor);
    }
}