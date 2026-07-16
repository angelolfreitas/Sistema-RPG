package com.ieji.rpg.infra.websocket;

import com.ieji.rpg.infra.security.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // Onde o front vai escutar
        config.setApplicationDestinationPrefixes("/app"); // Onde o front vai enviar
    }

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    try {
                        String authHeader = accessor.getFirstNativeHeader("Authorization");

                        if (authHeader != null && authHeader.startsWith("Bearer ")) {
                            String token = authHeader.replace("Bearer ", "");
                            String login = tokenService.validateToken(token);

                            if (login != null) {
                                UserDetails user = userDetailsService.loadUserByUsername(login);
                                UsernamePasswordAuthenticationToken authentication =
                                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                                accessor.setUser(authentication);

                                System.out.println("🟢 WEBSOCKET SUCESSO: Usuário [" + login + "] entrou na mesa!");
                            } else {
                                System.out.println("🔴 WEBSOCKET ERRO: Token JWT retornou nulo (inválido ou expirado).");
                            }
                        } else {
                            System.out.println("🟡 WEBSOCKET AVISO: Tentativa de conexão sem o header Authorization.");
                        }
                    } catch (Exception e) {
                        System.out.println("💥 WEBSOCKET EXCEPTION: Ocorreu um erro ao autenticar: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                return message;
            }
        });
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(frontendUrl) // <-- Coloque a URL exata do React aqui
                .withSockJS();
    }
}