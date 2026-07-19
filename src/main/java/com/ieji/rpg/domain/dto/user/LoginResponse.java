package com.ieji.rpg.domain.dto.user;

import com.ieji.rpg.domain.entity.role.Role;
/// DTO de response retornado após login ou cadastro bem-sucedido.
/// possui:
/// Integer: id — id do usuário
/// String: username
/// String: token — token JWT gerado para a sessão
/// Role: role
public record LoginResponse(Integer id, String username, String token, Role role) {
}