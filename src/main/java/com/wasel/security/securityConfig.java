package com.wasel.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class securityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // تعطيل CSRF
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // السماح لكل الطلبات
                )
                .httpBasic(Customizer.withDefaults()); // مهم أحياناً

        return http.build();
    }
}