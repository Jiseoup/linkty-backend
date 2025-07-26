package com.linkty.config;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.linkty.jwt.JwtAuthFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    // User password encoder for security.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Security filter chain for handling HTTP security configurations.
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Enable CORS with custom CORS configuration.
                .cors(Customizer.withDefaults())

                // Disable CSRF protection.
                .csrf(AbstractHttpConfigurer::disable)

                // Disable default HTTP Basic authentication dialog.
                .httpBasic(AbstractHttpConfigurer::disable)

                // Disable session creation, use stateless JWT authentication.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configure authorization rules for incoming HTTP requests.
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/{shortenUrl}", "/shorten-url",
                                "/email/verification",
                                "/email/verification/confirm", "/user/register",
                                "/user/login", "/user/refresh-token")
                        .permitAll().anyRequest().authenticated())

                // Add JWT authentication filter before UsernamePasswordAuthenticationFilter.
                .addFilterBefore(jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Configure CORS settings for all endpoints.
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Allow requests from specified origins.
        corsConfiguration.setAllowedOrigins(
                List.of("http://localhost:3000", "https://linkty.kr"));

        // Allow specified HTTP methods.
        corsConfiguration.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Allow specified headers in the request.
        corsConfiguration
                .setAllowedHeaders(List.of("Authorization", "Content-Type"));

        // Allow credentials. (ex. cookies, authorization headers)
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }
}
