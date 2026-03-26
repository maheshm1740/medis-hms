package com.medis.hospital_system.infrastructure.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/v1/users/register",
                                "/api/v1/appointments/**",
                                "/api/v1/doctors/**",
                                "/api/v1/patients/**",
                                "/api/v1/medicines/**",
                                "/api/v1/inventory/**"
                        ).permitAll()
                        .requestMatchers("/api/v1/users/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );
        return http.build();
    }
}