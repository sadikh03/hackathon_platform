package com.hackathon.hackathon_platform.security;

import com.hackathon.hackathon_platform.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // Permet de récupérer l'entité User d'origine depuis les contrôleurs
    public User getUser() {
        return user;
    }

    // Spring Security a besoin d'une liste de "permissions" (rôles).
    // On y met le rôle unique de notre utilisateur.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // Les 4 méthodes suivantes gèrent des cas qu'on ne traite pas dans ce projet
    // (compte expiré, compte bloqué...) — on renvoie donc "true" partout (= compte toujours actif)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
