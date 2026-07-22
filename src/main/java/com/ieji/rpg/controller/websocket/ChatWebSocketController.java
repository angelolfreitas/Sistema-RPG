package com.ieji.rpg.controller.websocket;

import com.ieji.rpg.domain.dto.mensagem.MensagemChatRequest;
import com.ieji.rpg.domain.dto.mensagem.MensagemChatResponse;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.service.MensagemChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;

/// Responsável apenas por receber e retransmitir mensagens de chat de um caso.
///
/// O autor da mensagem é SEMPRE resolvido a partir do usuário autenticado na
/// conexão STOMP (associado no handshake por JwtHandshakeInterceptor), nunca
/// a partir do authorId enviado no payload — isso evita que um cliente
/// malicioso envie mensagens em nome de outro usuário.
@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MensagemChatService chatService;

    @MessageMapping("/caso/{casoId}/chat")
    @SendTo("/topic/caso/{casoId}/chat")
    public MensagemChatResponse receberMensagem(@DestinationVariable Integer casoId,
                                                MensagemChatRequest request,
                                                Principal principal) {
        Usuario autor = extrairUsuario(principal);
        if (autor == null) {
            throw new AccessDeniedException("Usuário não autenticado no socket.");
        }
        return chatService.salvarMensagem(casoId, autor.getId(), request.personagemId(), request.conteudo());
    }

    @MessageExceptionHandler(AccessDeniedException.class)
    public void tratarAcessoNegado(AccessDeniedException e) {
        log.warn("WebSocket chat: mensagem rejeitada — {}", e.getMessage());
    }

    private Usuario extrairUsuario(Principal principal) {
        if (principal instanceof Authentication auth && auth.getPrincipal() instanceof Usuario usuario) {
            return usuario;
        }
        return null;
    }
}