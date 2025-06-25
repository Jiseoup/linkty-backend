package com.urlshortener.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // User password encoder for security.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security filter chain for handling HTTP security configurations.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF protection.
            .csrf(AbstractHttpConfigurer::disable)

            // Disable default HTTP Basic authentication dialog.
            .httpBasic(AbstractHttpConfigurer::disable)

            // Configure authorization rules for incoming HTTP requests.
            .authorizeHttpRequests((authorize) -> authorize
                    .requestMatchers("/shorten", "/{shortenUrl}", "/user/**").permitAll()
                    .anyRequest().authenticated()
            );

        return http.build();
    }
}
