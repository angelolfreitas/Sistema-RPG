package com.ieji.rpg.domain.dto.user;

import com.ieji.rpg.domain.entity.role.Role;
/// DTO de response usado para exibir um usuário presente/cadastrado no sistema.
/// possui:
/// Integer: id
/// String: username
/// String: email
/// Role: role
public record UsuarioPresente(Integer id, String username, String email, Role role) {}