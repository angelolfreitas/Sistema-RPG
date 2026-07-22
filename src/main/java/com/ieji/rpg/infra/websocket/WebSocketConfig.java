package com.ieji.rpg.infra.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/// configuração do WebSocket (STOMP) da aplicação.
/// O webscoekt permite o envio de requisições para usuárioss em depender deles.
/// O STOMP facilita o trabalho com o websocket como se fosse um email.
/// /app: requisicoes dos clientes
/// /topic: respostas do servidor
/// habilita o broker (servidor receptor) simples em "/topic" e o prefixo "/app" para mensagens
/// enviadas pelo cliente.
/// registra um interceptor no canal de entrada que autentica a conexão
/// via JWT no momento do CONNECT, extraindo o token do header Authorization,
/// validando-o e associando o usuário autenticado ao accessor da sessão STOMP.
/// registra o endpoint público "/ws" (com SockJS) liberado para o front-end
/// configurado e para subdomínios de preview da Vercel.
///
///
/// EM suma, o front requere para o /app. A primeira requsição é uma tentativa de requisição.
/// Nesse momento, o JwtHandshakeInterceptor verifica a autorização do usuário
/// pelo token service. Se ele for autenticado, permite todas as requisições e leituras do usuário ao websocket. Se nao,
/// proíbe a conexão do usuário.

@Configuration
@EnableWebSocketMessageBroker///habilita a central de mensagens do websocket
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)//// Define a injeção dese componente como uma das mais priorizáveis
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {



    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    @Value("${app.frontend-url}")
    private String frontendUrl;
    /// configura as rebdpoints de respostas e requisições
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }
    ///Aqui, define o métodod e filtragem das requsições feitas ao websocket
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(jwtHandshakeInterceptor);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(
                        frontendUrl,
                        "https://*-ieji.vercel.app"
                )
                .withSockJS();
    }


}