package com.jinjjaseoul.config;

import com.jinjjaseoul.auth.jwt.JwtAccessDeniedHandler;
import com.jinjjaseoul.auth.jwt.JwtAuthenticationEntryPoint;
import com.jinjjaseoul.auth.jwt.JwtFilter;
import com.jinjjaseoul.auth.oauth2.OAuth2AuthenticationFailureHandler;
import com.jinjjaseoul.auth.oauth2.OAuth2AuthenticationSuccessHandler;
import com.jinjjaseoul.auth.oauth2.OAuth2AuthorizationRequestRepository;
import com.jinjjaseoul.auth.oauth2.OAuth2UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final OAuth2UserServiceImpl oAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final OAuth2AuthorizationRequestRepository oAuth2AuthorizationRequestRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .cors().disable()
                .authorizeRequests()
                .anyRequest().permitAll();

        httpSecurity
                .headers()
                .frameOptions()
                .sameOrigin()

                .and()

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)

                .and()

                .formLogin()
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/")

                .and()

                .logout()
                .logoutUrl("/logout")
                .clearAuthentication(true)
                .logoutSuccessUrl("/")

                .and()

                .oauth2Login()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler)
                .userInfoEndpoint()
                .userService(oAuth2UserService)

                .and()

                .authorizationEndpoint()
                .baseUri("/oauth2/authorization")
                .authorizationRequestRepository(oAuth2AuthorizationRequestRepository)

                .and()

                .redirectionEndpoint()
                .baseUri("/*/oauth2/code/*");

        return httpSecurity.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}