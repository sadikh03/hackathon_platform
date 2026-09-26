package com.hackathon.hackathon_platform.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Récupérer le header Authorization
        String authHeader = request.getHeader("Authorization");

        // 2. S'il n'y a pas de token, on laisse passer la requête telle quelle
        //    (elle sera bloquée plus tard si la route nécessite d'être connecté)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extraire le token (on enlève le préfixe "Bearer ")
        String token = authHeader.substring(7);

        // 4. Vérifier que le token est valide
        if (jwtUtils.validateToken(token)) {

            String username = jwtUtils.getUsernameFromToken(token);

            // 5. Charger les détails de l'utilisateur depuis la base
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 6. Construire un objet "Authentication" que Spring Security comprend
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,               // le principal (qui est connecté)
                            null,                       // pas besoin du mot de passe ici
                            userDetails.getAuthorities() // ses rôles/permissions
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // 7. Enregistrer cette authentification dans le "contexte" de la requête en cours
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 8. Laisser la requête continuer sa route, authentifiée ou non
        filterChain.doFilter(request, response);
    }
}
