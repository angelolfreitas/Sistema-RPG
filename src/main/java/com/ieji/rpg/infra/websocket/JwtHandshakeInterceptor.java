package com.ieji.rpg.infra.websocket;

import com.ieji.rpg.infra.security.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
///injeta loger
/// interceptor responsável por autenticar a conexão STOMP no momento do CONNECT.
/// se o header Authorization estiver ausente, malformado, ou o token for
/// inválido/expirado, a conexão é rejeitada (retorno null).
/// se tudo estiver correto, associa o usuário autenticado ao accessor,
/// permitindo que os demais handlers da aplicação identifiquem o usuário
/// da sessão WebSocket.
///
/// Ele é o security filter do websocket.
public class JwtHandshakeInterceptor implements ChannelInterceptor {
    private static final String BEARER_PREFIX = "Bearer ";
    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(@NonNull Message<?> message, @NonNull MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null || !StompCommand.CONNECT.equals(accessor.getCommand())) {
            return message;
        }

        return autenticar(accessor) ? message : null;
    }

    private boolean autenticar(StompHeaderAccessor accessor) {
        try {
            String token = extrairToken(accessor);
            if (token == null) {
                log.warn("WebSocket: tentativa de conexão sem header Authorization válido.");
                return false;
            }

            String login = tokenService.validateToken(token);
            if (login == null) {
                log.warn("WebSocket: token JWT inválido ou expirado.");
                return false;
            }

            UserDetails user = userDetailsService.loadUserByUsername(login);
            accessor.setUser(new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities()));

            log.info("WebSocket: usuário [{}] autenticado com sucesso.", login);
            return true;

        } catch (Exception e) {
            log.error("WebSocket: erro ao autenticar conexão.", e);
            return false;
        }
    }

    private String extrairToken(StompHeaderAccessor accessor) {
        String authHeader = accessor.getFirstNativeHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authHeader.substring(BEARER_PREFIX.length());
    }
}