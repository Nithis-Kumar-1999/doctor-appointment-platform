package com.healthcare.appointment.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.healthcare.appointment.security.jwt.JwtAuthenticationEntryPoint;
import com.healthcare.appointment.security.jwt.JwtAuthenticationFilter;

import java.util.List;

/**
 * Foundation configuration for Spring Security.
 *
 * <p>This configuration establishes the core security boundary for the application:
 * <ul>
 *   <li>Disables CSRF (safe for stateless REST APIs).</li>
 *   <li>Enforces stateless session management (no JSESSIONID cookies).</li>
 *   <li>Sets up global CORS rules.</li>
 *   <li>Defines public endpoints vs protected endpoints.</li>
 *   <li>Wires up the AuthenticationProvider and PasswordEncoder.</li>
 * </ul>
 *
 * <p>Note: The JWT Filter is omitted from this commit as per the incremental
 * implementation plan. Currently, the application secures endpoints but lacks
 * the mechanism to extract tokens from requests.
 *
 * @author Nithish Kumar
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint jwtAuthEntryPoint;

    /**
     * Constructs the main security filter chain.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Disable CSRF — unnecessary for stateless REST APIs not using browser cookies
                .csrf(AbstractHttpConfigurer::disable)
                
                // 2. Apply CORS configuration
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // 3. Configure Exception Handling for unauthenticated requests
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthEntryPoint))
                
                // 4. Set session management to STATELESS — Spring Security will not create or use HTTP sessions
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                // 4. Define Endpoint Authorization Rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (Authentication & Documentation)
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )
                
                // 6. Register the Authentication Provider
                .authenticationProvider(authenticationProvider())
                
                // 7. Add JWT Filter before the standard UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configures the Data Access Object (DAO) Authentication Provider.
     * Uses our CustomUserDetailsService to load user data and BCrypt to verify passwords.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Exposes the AuthenticationManager as a Bean so it can be injected into the AuthService later.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Defines BCrypt as the password hashing algorithm.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Defines global CORS (Cross-Origin Resource Sharing) rules.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // In a strict production environment, this should be set to the specific frontend URL.
        // Using "*" temporarily for development ease, or specific headers for broader support.
        configuration.setAllowedOriginPatterns(List.of("*")); 
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply this configuration to all endpoints
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
