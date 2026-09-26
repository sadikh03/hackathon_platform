package com.hackathon.hackathon_platform.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    // Récupère la valeur définie dans application.properties (jwt.secret)
    @Value("${jwt.secret}")
    private String jwtSecret;

    // Récupère la durée de validité (jwt.expiration), en millisecondes
    @Value("${jwt.expiration}")
    private long jwtExpirationMs;

    // Transforme notre clé secrète (texte) en objet SecretKey utilisable pour signer
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // 1. GÉNÉRER un token à partir du username et du rôle
    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(username)              // "sub" = à qui appartient le token
                .claim("role", role)            // on ajoute le rôle dans le payload
                .issuedAt(now)                   // date de création
                .expiration(expiryDate)          // date d'expiration
                .signWith(getSigningKey())       // signature avec notre clé secrète
                .compact();                      // génère la chaîne finale
    }

    // 2. EXTRAIRE le username depuis un token
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // 3. EXTRAIRE le rôle depuis un token
    public String getRoleFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    // 4. VÉRIFIER qu'un token est valide (signature OK + non expiré)
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // token invalide, expiré, malformé, signature incorrecte...
            return false;
        }
    }
}
