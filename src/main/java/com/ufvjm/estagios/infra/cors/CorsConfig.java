package com.ufvjm.estagios.infra.cors;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:3000",
                        "http://127.0.0.1:3000", "https://sistema-de-estagios.vercel.app/") //mudar para o dominio do front
                .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH",  "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}