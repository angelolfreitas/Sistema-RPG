package com.ieji.rpg.domain.dto.user;


import com.ieji.rpg.domain.entity.role.Role;

public record UsuarioPresente(Integer id, String username, String email, Role role) {}