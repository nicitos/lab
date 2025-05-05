package com.example.demo11.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Weather API",
                version = "1.0",
                description = "API для управления погодными данными, городами и получения текущей погоды"
        )
)
@Configuration
public class SwaggerConfig {
}