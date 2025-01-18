package com.intellipick.intern8th.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openApi() {
        final String jwtSecurityName = "Authorization";
        return new OpenAPI()
                .components(
                        new Components().addSecuritySchemes(jwtSecurityName, securityScheme())
                )
                .info(swaggerInfo());
    }

    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .name("Authorization")
                .type(SecurityScheme.Type.HTTP)
                .scheme("Bearer")
                .bearerFormat("JWT");
    }

    // 문서 기본 정보
    private Info swaggerInfo() {
        return new Info()
                .title("한달인턴 8기 swagger api 문서")
                .summary("조준호")
                .version("1.0.0");
    }
}
