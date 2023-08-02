package com.backend.curi.common.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    SecurityScheme refreshToken = new SecurityScheme()
            .type(SecurityScheme.Type.APIKEY).in(SecurityScheme.In.COOKIE).name("refreshToken");

    SecurityScheme authToken = new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT");
    SecurityRequirement securityRequirement = new SecurityRequirement().addList("refreshToken").addList("authToken");

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("refreshToken", refreshToken)
                        .addSecuritySchemes("authToken", authToken))
                .addSecurityItem(securityRequirement)
                .info(new Info()
                        .title("Curi Backend API")
                        .description("Documentation of Curi Backend API")
                        .version("1.0")
                );

    }
}