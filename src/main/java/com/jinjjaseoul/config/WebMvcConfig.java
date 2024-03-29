package com.jinjjaseoul.config;

import io.jsonwebtoken.Header;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final long MAX_AGE_SECS = 3600L;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowCredentials(true)
                .exposedHeaders(Header.JWT_TYPE)
                .maxAge(MAX_AGE_SECS);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
        registry.addViewController("/sign-up").setViewName("join");
    }

    @Bean
    public HttpSessionRequestCache httpSessionRequestCache() {
        return new HttpSessionRequestCache();
    }
}