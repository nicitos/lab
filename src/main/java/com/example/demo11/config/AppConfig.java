package com.example.demo11.config;

import com.example.demo11.service.AccessCounter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public AccessCounter accessCounter() {
        return new AccessCounter();
    }
}