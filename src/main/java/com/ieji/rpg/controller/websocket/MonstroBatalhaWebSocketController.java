package com.ieji.rpg.controller.websocket;

import com.ieji.rpg.domain.dto.monstro.MonstroResponse;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.service.AutorizacaoService;
import com.ieji.rpg.service.monstro.MonstroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

/// Responsável por atualizações de vida e estado de batalha dos monstros
/// de um caso, notificando os jogadores conectados.
///
/// Antes, nenhum desses endpoints checava se quem mandava o comando era
/// mestre do caso — qualquer usuário autenticado no socket podia alterar PV
/// ou iniciar/encerrar batalha de qualquer monstro. Agora exigimos
/// admin::write, a mesma régua já usada nos endpoints REST equivalentes.
@Slf4j
@Controller
@RequiredArgsConstructor
public class MonstroBatalhaWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MonstroService monstroService;
    private final AutorizacaoService autorizacaoService;

    @MessageMapping("/caso/{casoId}/monstro/update")
    public void atualizarMonstro(@DestinationVariable Integer casoId,
                                 Map<String, Object> payload,
                                 Principal principal) {
        exigirAdmin(principal);

        Integer monstroId = (Integer) payload.get("id");
        Integer novoPv = (Integer) payload.get("pv");

        MonstroResponse atualizado = monstroService.alterarVida(monstroId, novoPv);
        messagingTemplate.convertAndSend(WebSocketTopics.monstros(casoId), atualizado);
    }

    @MessageMapping("/caso/{casoId}/monstro/{monstroId}/batalha")
    public void iniciarBatalha(@DestinationVariable Integer casoId,
                               @DestinationVariable Integer monstroId,
                               Principal principal) {
        exigirAdmin(principal);

        monstroService.registrarConhecimentoParaTodos(monstroId);
        MonstroResponse monstro = monstroService.marcarEmBatalha(monstroId, true);
        messagingTemplate.convertAndSend(WebSocketTopics.batalha(casoId), monstro);
    }

    @MessageMapping("/caso/{casoId}/monstro/{monstroId}/encerrar-batalha")
    public void encerrarBatalha(@DestinationVariable Integer casoId,
                                @DestinationVariable Integer monstroId,
                                Principal principal) {
        exigirAdmin(principal);

        MonstroResponse monstro = monstroService.marcarEmBatalha(monstroId, false);
        messagingTemplate.convertAndSend(WebSocketTopics.batalha(casoId), monstro);
    }

    @MessageExceptionHandler(AccessDeniedException.class)
    public void tratarAcessoNegado(AccessDeniedException e) {
        log.warn("WebSocket batalha: comando rejeitado — {}", e.getMessage());
    }

    private void exigirAdmin(Principal principal) {
        Usuario usuario = extrairUsuario(principal);
        if (usuario == null || !autorizacaoService.ehAdmin(usuario)) {
            throw new AccessDeniedException("Apenas o mestre (admin) pode controlar a batalha.");
        }
    }

    private Usuario extrairUsuario(Principal principal) {
        if (principal instanceof Authentication auth && auth.getPrincipal() instanceof Usuario usuario) {
            return usuario;
        }
        return null;
    }
}