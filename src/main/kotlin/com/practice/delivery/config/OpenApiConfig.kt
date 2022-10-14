package com.practice.delivery.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Bean
    fun openAPi(): OpenAPI {
        var info = Info()
        info.title = "Delivery API"
        info.version = "v1.0.0"
        info.description = "Spring Boot 를 이용한 배달앱 서비스 백엔드 API"
        info.contact = Contact().name("Tbread").email("itaebread@gmail.com").url("https://github.com/tbread")
        var securityScheme = SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
            .`in`(SecurityScheme.In.HEADER).name("Authorization")
        var securityRequirement = SecurityRequirement().addList("bearerAuth")
        return OpenAPI().info(info).components(Components().addSecuritySchemes("bearerAuth", securityScheme)).security(listOf(securityRequirement))
    }
}