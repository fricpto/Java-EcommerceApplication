package com.example.ecommerce.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    // Password hashing
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Authentication manager (needed for login)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // Security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter)
            throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/api/auth/**").permitAll() // ✅ allow register/login
                        .requestMatchers("/api/user/**").authenticated()
                        .requestMatchers("/api/products").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // admin only
                        .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN") // user or admin
                        .requestMatchers("/api/items", "/api/items/**").permitAll()
                        /* .requestMatchers("/**").permitAll() */
                        // static and frontend assets (permit)
                        .requestMatchers("/", "/index.html", "/favicon.ico", "/vite.svg").permitAll()
                        .requestMatchers("/assets/**", "/static/**", "/css/**", "/js/**", "/images/**", "/webjars/**")
                        .permitAll()
                        .requestMatchers("/.well-known/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // CORS preflight
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form.disable()) // ✅ disable default login page
                .httpBasic(basic -> basic.disable()); // ✅ disable basic auth

        return http.build();
    }
}