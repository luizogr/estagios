package com.ufvjm.estagios.infra.cors;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI customOpenApi(){
        return new OpenAPI().info(new Info().title("API Estágios Sistemas de Informação")
                .version("V1")
                .description("API para gerenciamento de estágios do curso de Sistemas de Informação - UFVJM")
                .license(new License()
                        .name("MIT License")
                        .url("https://opensource.org/licenses/MIT")));
    }
}
