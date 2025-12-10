package com.example.aqisystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Allow credentials (necessary for cookies/sessions)
        config.setAllowCredentials(true);
        
        // Specify the exact Netlify URL instead of using a wildcard (*)
        // The frontend is deployed at: https://resilient-cactus-d4a9c1.netlify.app
        config.addAllowedOrigin("https://aqii.netlify.app"); 
        
        // Allow all headers and methods
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        
        // Register CORS configuration for all paths ("/**")
        source.registerCorsConfiguration("/**", config);
        
        return new CorsFilter(source);
    }
}
