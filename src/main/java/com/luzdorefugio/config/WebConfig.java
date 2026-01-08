package com.luzdorefugio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // Permitir todas as rotas
                        .allowedOrigins("http://localhost:4200") // Permitir o Angular
                        .allowedOrigins("http://luzdorefugio.zapto.org") // Permitir o Angular
                        .allowedOrigins("http://luzdorefugio.zapto.org:4200") // Permitir o Angular
                        .allowedOrigins("https://rarefactive-grace-unfamiliarly.ngrok-free.dev/") // Permitir o Angular
                        .allowedOrigins("https://rarefactive-grace-unfamiliarly.ngrok-free.dev") // Permitir o Angular
                        .allowedOrigins("http://192.168.1.238:4200") // Permitir o Angular
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH");
            }
        };
    }

}
