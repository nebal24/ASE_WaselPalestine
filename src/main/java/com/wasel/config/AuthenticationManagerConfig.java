//package com.wasel.config;
//
//import com.wasel.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
///**
// * Configuration class for authentication-related beans
// * Sets up Spring Security components for user authentication
// */
//@Configuration
//@RequiredArgsConstructor
//public class AuthenticationManagerConfig {
//
//    // Repository for accessing user data from database
//    private final UserRepository userRepository;
//
//    /**
//     * Creates a service that loads user-specific data during authentication
//     * Spring Security uses this to retrieve user details from database
//     *
//     * @return UserDetailsService implementation that finds users by email
//     */
//    @Bean
//    public UserDetailsService userDetailsService() {
//        // Lambda expression that takes username (email) and returns User object
//        return username -> userRepository.findByEmail(username)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
//    }
//
//    /**
//     * Configures password encoding mechanism
//     * BCrypt is a strong hashing function for secure password storage
//     *
//     * @return BCryptPasswordEncoder instance
//     */
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder(); // Strength: 10 (default)
//    }
//
//    /**
//     * Creates the authentication provider responsible for authenticating users
//     * DaoAuthenticationProvider validates username/password against database
//     *
//     * @return Configured AuthenticationProvider
//     */
//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        // Create DaoAuthenticationProvider with UserDetailsService
//        // This provider handles username/password authentication
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
//
//        // Set password encoder to verify encrypted passwords
//        authProvider.setPasswordEncoder(passwordEncoder());
//
//        return authProvider;
//    }
//
//    /**
//     * Exposes AuthenticationManager as a Spring bean
//     * AuthenticationManager is the main strategy for authentication
//     *
//     * @param config Spring's AuthenticationConfiguration
//     * @return AuthenticationManager instance
//     * @throws Exception if configuration fails
//     */
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
//        return config.getAuthenticationManager();
//    }
//}