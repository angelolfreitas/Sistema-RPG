package com.ieji.rpg.controller.websocket;

import com.ieji.rpg.domain.dto.user.UsuarioPresente;
import com.ieji.rpg.domain.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/// Responsável apenas por rastrear quem está online em cada caso (via
/// subscribe/disconnect do STOMP) e notificar os demais participantes.
///
/// Obs.: o mapa de presença é em memória, então só funciona corretamente
/// com uma única instância da aplicação. Se o app rodar com mais de uma
/// réplica, isso precisa virar um estado compartilhado (Redis, por ex.).
@Controller
@RequiredArgsConstructor
public class PresencaWebSocketController {

    private static final Pattern TOPICO_CASO = Pattern.compile("^/topic/caso/(\\d+)(?:/.*)?$");

    private final SimpMessagingTemplate messagingTemplate;
    private final ConcurrentHashMap<Integer, Map<Integer, UsuarioPresente>> presencaPorCaso = new ConcurrentHashMap<>();

    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        extrairIdCaso(accessor.getDestination()).ifPresent(casoId -> {
            Usuario usuario = extrairUsuario(event.getUser());
            if (usuario != null) {
                presencaPorCaso
                        .computeIfAbsent(casoId, k -> new ConcurrentHashMap<>())
                        .put(usuario.getId(), toDto(usuario));
                notificarPresenca(casoId);
            }
        });
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        Usuario usuario = extrairUsuario(event.getUser());
        if (usuario == null) {
            return;
        }
        presencaPorCaso.forEach((casoId, usuarios) -> {
            if (usuarios.remove(usuario.getId()) != null) {
                notificarPresenca(casoId);
            }
        });
    }

    private Optional<Integer> extrairIdCaso(String destination) {
        if (destination == null) {
            return Optional.empty();
        }
        Matcher matcher = TOPICO_CASO.matcher(destination);
        return matcher.matches() ? Optional.of(Integer.parseInt(matcher.group(1))) : Optional.empty();
    }

    private void notificarPresenca(Integer casoId) {
        Collection<UsuarioPresente> presentes = presencaPorCaso.getOrDefault(casoId, Map.of()).values();
        messagingTemplate.convertAndSend(WebSocketTopics.presenca(casoId), presentes);
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
}