package com.hackathon.hackathon_platform.service;

import com.hackathon.hackathon_platform.dto.JwtResponse;
import com.hackathon.hackathon_platform.dto.LoginRequest;
import com.hackathon.hackathon_platform.dto.RegisterRequest;
import com.hackathon.hackathon_platform.entity.User;
import com.hackathon.hackathon_platform.repository.UserRepository;
import com.hackathon.hackathon_platform.security.CustomUserDetails;
import com.hackathon.hackathon_platform.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    public void register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Ce username est déjà pris");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // JAMAIS en clair
                .role(request.getRole())
                .build();

        userRepository.save(user);
    }

    public JwtResponse login(LoginRequest request) {

        // Délègue à Spring Security la vérification username + password
        // Lève automatiquement une exception si les identifiants sont incorrects
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        String token = jwtUtils.generateToken(user.getUsername(), user.getRole().name());

        return new JwtResponse(token, user.getUsername(), user.getRole().name());
    }
}
