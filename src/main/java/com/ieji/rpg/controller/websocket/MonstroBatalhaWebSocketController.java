package com.ieji.rpg.controller.websocket;

import com.ieji.rpg.domain.dto.monstro.MonstroResponse;
import com.ieji.rpg.service.monstro.MonstroService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

/// Responsável por atualizações de vida e estado de batalha dos monstros
/// de um caso, notificando os jogadores conectados.
///
/// TODO: nenhum desses endpoints checa se quem está mandando o comando é o
/// mestre do caso — hoje qualquer usuário autenticado no socket pode
/// alterar PV ou iniciar/encerrar batalha de qualquer monstro. Vale aplicar
/// aqui a mesma checagem de autorização já usada nos controllers REST
/// (ex.: algo como monstroService.getComAcesso(monstroId, usuario)).
@Controller
@RequiredArgsConstructor
public class MonstroBatalhaWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MonstroService monstroService;

    @MessageMapping("/caso/{casoId}/monstro/update")
    public void atualizarMonstro(@DestinationVariable Integer casoId, Map<String, Object> payload) {
        Integer monstroId = (Integer) payload.get("id");
        Integer novoPv = (Integer) payload.get("pv");

        MonstroResponse atualizado = monstroService.alterarVida(monstroId, novoPv);
        messagingTemplate.convertAndSend(WebSocketTopics.monstros(casoId), atualizado);
    }

    @MessageMapping("/caso/{casoId}/monstro/{monstroId}/batalha")
    public void iniciarBatalha(@DestinationVariable Integer casoId, @DestinationVariable Integer monstroId) {
        monstroService.registrarConhecimentoParaTodos(monstroId);
        MonstroResponse monstro = monstroService.marcarEmBatalha(monstroId, true);
        messagingTemplate.convertAndSend(WebSocketTopics.batalha(casoId), monstro);
    }

    @MessageMapping("/caso/{casoId}/monstro/{monstroId}/encerrar-batalha")
    public void encerrarBatalha(@DestinationVariable Integer casoId, @DestinationVariable Integer monstroId) {
        MonstroResponse monstro = monstroService.marcarEmBatalha(monstroId, false);
        messagingTemplate.convertAndSend(WebSocketTopics.batalha(casoId), monstro);
    }
}