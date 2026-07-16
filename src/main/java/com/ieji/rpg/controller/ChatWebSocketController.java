package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.mensagem.MensagemChatRequest;
import com.ieji.rpg.domain.dto.mensagem.MensagemChatResponse;
import com.ieji.rpg.domain.dto.monstro.MonstroResponse;
import com.ieji.rpg.domain.dto.user.UsuarioPresente;
import com.ieji.rpg.domain.entity.Monstro;
import com.ieji.rpg.domain.entity.MonstroConhecido;
import com.ieji.rpg.domain.entity.Personagem;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.infra.repository.MonstroConhecidoRepository;
import com.ieji.rpg.infra.repository.MonstroRepository;
import com.ieji.rpg.infra.repository.PersonagemRepository;
import com.ieji.rpg.service.MensagemChatService;
import com.ieji.rpg.service.MonstroService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MensagemChatService chatService;
    private final MonstroService monstroService;
    private final MonstroConhecidoRepository monstroConhecidoRepository;
    private final MonstroRepository monstroRepository;
    private final PersonagemRepository personagemRepository;

    private final ConcurrentHashMap<Integer, Map<Integer, UsuarioPresente>> presencaPorCaso = new ConcurrentHashMap<>();

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();

        if (destination != null && destination.startsWith("/topic/caso/")) {
            Integer casoId = extrairIdCaso(destination);
            Usuario usuario = extrairUsuario(event.getUser());
            if (usuario != null) {
                presencaPorCaso
                        .computeIfAbsent(casoId, k -> new ConcurrentHashMap<>())
                        .put(usuario.getId(), toDto(usuario));
                notificarPresenca(casoId);
            }
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        Usuario usuario = extrairUsuario(event.getUser());
        if (usuario != null) {
            presencaPorCaso.forEach((casoId, usuarios) -> {
                if (usuarios.remove(usuario.getId()) != null) {
                    notificarPresenca(casoId);
                }
            });
        }
    }

    private void notificarPresenca(Integer casoId) {
        Collection<UsuarioPresente> presentes = presencaPorCaso.getOrDefault(casoId, Map.of()).values();
        messagingTemplate.convertAndSend("/topic/caso/" + casoId + "/presenca", presentes);
    }

    private Usuario extrairUsuario(Principal principal) {
        if (principal instanceof Authentication auth && auth.getPrincipal() instanceof Usuario usuario) {
            return usuario;
        }
        return null;
    }

    private UsuarioPresente toDto(Usuario usuario) {
        return new UsuarioPresente(usuario.getId(), usuario.getUsername(), usuario.getEmail(), usuario.getRole());
    }

    @MessageMapping("/caso/{casoId}/chat")
    @SendTo("/topic/caso/{casoId}/chat")
    public MensagemChatResponse receberMensagem(@DestinationVariable Integer casoId,
                                                MensagemChatRequest request) {
        return chatService.salvarMensagem(casoId, request.authorId(), request.personagemId(), request.conteudo());
    }

    private Integer extrairIdCaso(String destination) {
        String[] parts = destination.split("/");
        return Integer.parseInt(parts[3]);
    }

    @MessageMapping("/caso/{casoId}/monstro/update")
    public void atualizarMonstro(@DestinationVariable Integer casoId, Map<String, Object> payload) {
        Integer monstroId = (Integer) payload.get("id");
        Integer novoPv = (Integer) payload.get("pv");

        MonstroResponse updated = monstroService.alterarVida(monstroId, novoPv);

        messagingTemplate.convertAndSend("/topic/caso/" + casoId + "/monstros", updated);
    }

    @MessageMapping("/caso/{casoId}/monstro/{monstroId}/batalha")
    public void iniciarBatalha(@DestinationVariable Integer casoId, @DestinationVariable Integer monstroId) {
        List<Integer> usuariosOnlineIds = presencaPorCaso.getOrDefault(casoId, Map.of())
                .values().stream().map(UsuarioPresente::id).toList();
        monstroService.registrarConhecimentoParaUsuarios(monstroId, usuariosOnlineIds);
        MonstroResponse monstro = monstroService.marcarEmBatalha(monstroId, true);
        messagingTemplate.convertAndSend("/topic/caso/" + casoId + "/batalha", monstro);
    }

    @MessageMapping("/caso/{casoId}/monstro/{monstroId}/encerrar-batalha")
    public void encerrarBatalha(@DestinationVariable Integer casoId, @DestinationVariable Integer monstroId) {
        MonstroResponse monstro = monstroService.marcarEmBatalha(monstroId, false);
        messagingTemplate.convertAndSend("/topic/caso/" + casoId + "/batalha", monstro);
    }

    private void registrarConhecimento(Integer casoId, Integer monstroId) {
        Collection<UsuarioPresente> presentes = presencaPorCaso.getOrDefault(casoId, Map.of()).values();
        if (presentes.isEmpty()) return;

        Monstro monstro = monstroRepository.findById(monstroId).orElse(null);
        if (monstro == null) return;

        for (UsuarioPresente up : presentes) {
            List<Personagem> personagens = personagemRepository.findByUsuarioId(up.id());
            for (Personagem p : personagens) {
                boolean jaConhece = monstroConhecidoRepository
                        .existsByMonstro_IdMonstroAndPersonagem_Usuario_Id(monstroId, up.id());
                if (!jaConhece) {
                    monstroConhecidoRepository.save(MonstroConhecido.builder()
                            .monstro(monstro)
                            .personagem(p)
                            .conhecidoEm(Instant.now())
                            .build());
                }
            }
        }
    }
}