package com.wasel.config;

import com.wasel.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration class for the application
 * Configures JWT authentication, authorization rules, and security filters
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // Custom JWT filter to validate tokens on each request
    private final JwtAuthenticationFilter jwtAuthFilter;

    // Authentication provider for user authentication
    private final AuthenticationProvider authenticationProvider;

    /**
     * Configures the security filter chain
     * Defines which endpoints are public/private and adds JWT filter
     *
     * @param http HttpSecurity object to configure
     * @return SecurityFilterChain with all security configurations
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF protection since we're using stateless JWT
                .csrf(csrf -> csrf.disable())

                // Configure authorization rules for endpoints
                .authorizeHttpRequests(auth -> auth
                        // Allow all auth endpoints (register/login) without token
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )

                // Configure session management to be stateless (no sessions)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Set custom authentication provider
                .authenticationProvider(authenticationProvider)

                // Add JWT filter before Spring's default authentication filter
                // This ensures JWT is processed first
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }
}