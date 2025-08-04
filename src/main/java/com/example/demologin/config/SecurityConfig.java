package com.example.demologin.config;

import com.example.demologin.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private Filter filter;

    @Autowired
    private PublicEndpointHandlerMapping publicEndpointHandlerMapping;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Use allowedOriginPatterns OR allowedOrigins, not both
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(false); // Set to false for production
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Access-Control-Allow-Origin", "Access-Control-Allow-Methods", "Access-Control-Allow-Headers"));
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http , CustomOAuth2SuccessHandler customOAuth2SuccessHandler, CustomOAuth2FailureHandler customOAuth2FailureHandler) throws Exception {
        // Lấy danh sách các public endpoints từ annotation @PublicEndpoint
        List<String> annotatedPublicEndpoints = publicEndpointHandlerMapping.getPublicEndpoints();
        
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    // Cho phép preflight requests
                    auth.requestMatchers(CorsUtils::isPreFlightRequest).permitAll();
                    
                    // Tự động permit all các endpoints được đánh dấu @PublicEndpoint
                    if (!annotatedPublicEndpoints.isEmpty()) {
                        auth.requestMatchers(annotatedPublicEndpoints.toArray(new String[0])).permitAll();
                    }
                    
                    // Các endpoint hệ thống cần permit all (không thể dùng annotation)
                    auth.requestMatchers(
                            // Swagger/OpenAPI documentation
                            "/swagger-ui/**",
                            "/v3/api-docs/**", 
                            "/swagger-resources/**",
                            "/webjars/**",
                            // OAuth2 system endpoints (Spring Security tự động tạo)
                            "/login/oauth2/code/**",
                            "/oauth2/authorization/**"
                    ).permitAll();
                    
                    // Tất cả các API endpoints khác cần authentication
                    // Filter sẽ handle JWT validation + dynamic permission với @SecuredEndpoint
                    auth.requestMatchers("/api/**").authenticated();
                    auth.anyRequest().authenticated();
                })
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/oauth2/authorization/google")
                        .defaultSuccessUrl("/api/oauth2/success")
                        .failureUrl("/api/oauth2/failure")
                        .successHandler(customOAuth2SuccessHandler)
                        .failureHandler(customOAuth2FailureHandler)
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .userDetailsService(authenticationService)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
