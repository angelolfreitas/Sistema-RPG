package com.ieji.rpg.service;

import com.ieji.rpg.domain.entity.Usuario;
import org.springframework.stereotype.Component;

/// Único lugar que decide "esse usuário é mestre (manager ou admin)?".
/// Antes essa regra estava duplicada em PersonagemService, InventarioController
/// e MonstroService — e a cópia do MonstroService tinha um bug: checava
/// "admin::write" duas vezes em vez de "manager::write" OU "admin::write",
/// então um MANAGER era tratado como jogador comum na listagem de monstros.
@Component
public class AutorizacaoService {

    public boolean ehMestre(Usuario usuario) {
        return usuario.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("manager::write") || a.getAuthority().equals("admin::write"));
    }
}