package com.smartMunicipal.Smart.Municipal.Services.Config;

import com.smartMunicipal.Smart.Municipal.Services.ServiceImpl.MyUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired
    private MyUserDetailService myUserDetailService;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        // Public authentication endpoints
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/login", "/register").permitAll()
                        
                        // Public API endpoints
                        .requestMatchers("/api/documents/verify/**").permitAll() // QR code verification with path variables
                        .requestMatchers("/api/feedback/rating").permitAll() // Public rating view
                        
                        // Complaint endpoints - accessible to authenticated users
                        .requestMatchers("/api/complaints/raise").authenticated()
                        .requestMatchers("/api/complaints").authenticated()
                        .requestMatchers("/api/complaints/*/complete").hasRole("ADMIN") // Admin only
                        .requestMatchers("/api/complaints/*").hasRole("ADMIN") // Delete - Admin only
                        
                        // Feedback endpoints - authenticated users
                        .requestMatchers("/api/feedback").authenticated()
                        
                        // Document endpoints
                        .requestMatchers("/api/documents/request").authenticated()
                        .requestMatchers("/api/documents/issue/*").hasRole("ADMIN") // Admin only
                        
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .formLogin(Customizer.withDefaults()) // Use built-in form login (or customize)
                .httpBasic(Customizer.withDefaults()); // Enable HTTP Basic for API testing

        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }




}
