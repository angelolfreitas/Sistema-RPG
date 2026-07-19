package com.ieji.rpg.controller.websocket;

import com.ieji.rpg.domain.dto.mensagem.MensagemChatRequest;
import com.ieji.rpg.domain.dto.mensagem.MensagemChatResponse;
import com.ieji.rpg.service.MensagemChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

/// Responsável apenas por receber e retransmitir mensagens de chat de um caso.
@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MensagemChatService chatService;

    @MessageMapping("/caso/{casoId}/chat")
    @SendTo("/topic/caso/{casoId}/chat")
    public MensagemChatResponse receberMensagem(@DestinationVariable Integer casoId,
                                                MensagemChatRequest request) {
        return chatService.salvarMensagem(casoId, request.authorId(), request.personagemId(), request.conteudo());
    }
}