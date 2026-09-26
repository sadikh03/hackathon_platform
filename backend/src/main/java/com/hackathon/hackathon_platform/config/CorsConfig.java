package com.hackathon.hackathon_platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        // Origines autorisées à appeler notre API
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        // Méthodes HTTP autorisées
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Headers autorisés dans les requêtes (notamment "Authorization" pour notre JWT)
        configuration.setAllowedHeaders(List.of("*"));

        // Autorise l'envoi de credentials (utile si on utilisait des cookies, pas indispensable avec JWT mais sans risque)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // s'applique à toutes les routes

        return source;
    }
}
