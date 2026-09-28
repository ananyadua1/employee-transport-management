package com.ananya.transport.config;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.components.Components;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.*;

@Configuration public class OpenApiConfig {
    @Bean OpenAPI api() { return new OpenAPI().info(new Info().title("Employee Transport Management API").version("1.0.0").description("JWT-secured transport scheduling and booking backend"))
        .components(new Components().addSecuritySchemes("bearerAuth",new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
        .addSecurityItem(new SecurityRequirement().addList("bearerAuth")); }
}
