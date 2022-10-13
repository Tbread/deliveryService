package com.practice.delivery.config

import com.practice.delivery.utils.CustomInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport


@Configuration
class WebMvcConfig: WebMvcConfigurationSupport() {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(CustomInterceptor())
            .addPathPatterns("**")
    }

}