package com.hackathon.hackathon_platform.config;

import com.hackathon.hackathon_platform.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // API REST stateless : pas besoin de protection CSRF (celle-ci sert
                // surtout aux formulaires HTML classiques avec sessions/cookies)
                .csrf(csrf -> csrf.disable())

                .cors(cors -> {}) // ← nouvelle ligne : active CORS avec le bean CorsConfigurationSource qu'on vient de créer

                // On dit à Spring Security de ne JAMAIS créer de session HTTP.
                // Chaque requête doit se ré-authentifier via son propre token JWT.
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Définition des règles d'accès, route par route
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ws/**", "/ws-native/**").permitAll()
                        // Routes publiques : pas besoin d'être connecté
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // Routes réservées à un rôle précis
                        .requestMatchers("/api/jury/**").hasRole("JURY")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Toutes les autres routes nécessitent simplement d'être connecté
                        .anyRequest().authenticated()
                )

                // On insère notre filtre JWT AVANT le filtre standard de Spring Security
                // qui gère l'authentification par username/password
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Bean utilisé partout où on doit hacher ou vérifier un mot de passe
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Bean utilisé dans AuthController pour vérifier les identifiants au login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
