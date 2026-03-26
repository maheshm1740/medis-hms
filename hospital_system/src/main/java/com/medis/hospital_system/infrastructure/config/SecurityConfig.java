package com.medis.hospital_system.infrastructure.config;

import com.medis.hospital_system.infrastructure.security.JwtAuthenticationFilter;
import com.medis.hospital_system.infrastructure.security.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsServiceImpl userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          UserDetailsServiceImpl userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth

                        // ── Public ────────────────────────────────────────────
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users/register").permitAll()

                        // ── Users ─────────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST,  "/api/v1/users/admin/create").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET,   "/api/v1/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/*/password").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/users/*/phone").authenticated()

                        // ── Doctors — /me first (specific before wildcard) ────
                        .requestMatchers(HttpMethod.GET,    "/api/v1/doctors/me").hasRole("DOCTOR")
                        .requestMatchers(HttpMethod.POST,   "/api/v1/doctors/**").hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.GET,    "/api/v1/doctors/**").hasAnyRole("ADMIN", "DOCTOR", "RECEPTIONIST", "PATIENT")
                        .requestMatchers(HttpMethod.PATCH,  "/api/v1/doctors/**").hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/doctors/**").hasRole("ADMIN")

                        // ── Patients — /me first (specific before wildcard) ───
                        .requestMatchers(HttpMethod.GET,    "/api/v1/patients/me").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.POST,   "/api/v1/patients/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.GET,    "/api/v1/patients/**").hasAnyRole("ADMIN", "DOCTOR", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.PATCH,  "/api/v1/patients/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/patients/**").hasRole("ADMIN")

                        // ── Appointments ──────────────────────────────────────
                        .requestMatchers(HttpMethod.POST,  "/api/v1/appointments/**").hasAnyRole("ADMIN", "RECEPTIONIST", "PATIENT")
                        .requestMatchers(HttpMethod.GET,   "/api/v1/appointments/**").hasAnyRole("ADMIN", "DOCTOR", "RECEPTIONIST", "PATIENT")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/appointments/**").hasAnyRole("ADMIN", "RECEPTIONIST", "DOCTOR")

                        // ── Medicines ─────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST,  "/api/v1/medicines/**").hasAnyRole("ADMIN", "PHARMACIST")
                        .requestMatchers(HttpMethod.GET,   "/api/v1/medicines/**").hasAnyRole("ADMIN", "PHARMACIST", "DOCTOR")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/medicines/**").hasAnyRole("ADMIN", "PHARMACIST")

                        // ── Inventory ─────────────────────────────────────────
                        .requestMatchers(HttpMethod.POST,   "/api/v1/inventory/**").hasAnyRole("ADMIN","PHARMACIST")
                        .requestMatchers(HttpMethod.GET,    "/api/v1/inventory/**").hasAnyRole("ADMIN", "PHARMACIST")
                        .requestMatchers(HttpMethod.PATCH,  "/api/v1/inventory/**").hasAnyRole("ADMIN", "PHARMACIST")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/inventory/**").hasAnyRole("ADMIN", "PHARMACIST")

                        // ── Fallback ──────────────────────────────────────────
                        .anyRequest().authenticated()
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}