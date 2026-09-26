package com.hackathon.hackathon_platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Le "point d'entrée" que le client Angular va utiliser pour se connecter
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:4200")
                .withSockJS(); // active le repli SockJS si besoin

        // Endpoint WebSocket natif, sans SockJS
        registry.addEndpoint("/ws-native")
                .setAllowedOrigins("http://localhost:4200");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Tout ce qui commence par /topic est géré par le "broker" mémoire de Spring
        // (il se charge de diffuser aux abonnés, on n'a rien à coder pour ça)
        registry.enableSimpleBroker("/topic");
    }
}