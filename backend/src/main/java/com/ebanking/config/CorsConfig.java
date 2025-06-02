package com.ebanking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

/**
 * CORS Configuration for the application
 * Handles Cross-Origin Resource Sharing for frontend-backend communication
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${spring.web.cors.allowed-origins:http://localhost:45571}")
    private String allowedOrigins;

    @Value("${spring.web.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    @Value("${spring.web.cors.allowed-headers:*}")
    private String allowedHeaders;

    @Value("${spring.web.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Value("${spring.web.cors.max-age:3600}")
    private long maxAge;

    /**
     * Configure CORS for Spring MVC
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        if (allowedHeaders.equals("*")) {
            registry.addMapping("/api/**")
                    .allowedOrigins(allowedOrigins.split(","))
                    .allowedMethods(allowedMethods.split(","))
                    .allowedHeaders("*")
                    .allowCredentials(allowCredentials)
                    .maxAge(maxAge);
        } else {
            registry.addMapping("/api/**")
                    .allowedOrigins(allowedOrigins.split(","))
                    .allowedMethods(allowedMethods.split(","))
                    .allowedHeaders(allowedHeaders.split(","))
                    .allowCredentials(allowCredentials)
                    .maxAge(maxAge);
        }
    }

    /**
     * CORS Configuration Source for Spring Security
     * This is needed because Spring Security processes requests before Spring MVC
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Set allowed origins
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));

        // Set allowed methods
        configuration.setAllowedMethods(Arrays.asList(allowedMethods.split(",")));

        // Set allowed headers
        if (allowedHeaders.equals("*")) {
            configuration.addAllowedHeader("*");
        } else {
            configuration.setAllowedHeaders(Arrays.asList(allowedHeaders.split(",")));
        }

        // Set credentials
        configuration.setAllowCredentials(allowCredentials);

        // Set max age
        configuration.setMaxAge(maxAge);

        // Apply configuration to all API paths
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);

        return source;
    }
}
