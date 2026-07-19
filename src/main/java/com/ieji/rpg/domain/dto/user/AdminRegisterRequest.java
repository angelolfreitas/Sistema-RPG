package com.ieji.rpg.domain.dto.user;

import com.ieji.rpg.domain.entity.role.Role;
/// DTO de request usado para registrar um novo administrador.
/// precisa de um:
/// LoginRequest: usuario — dados de login/cadastro do novo admin
/// Role: role — papel a ser atribuído (ex.: ADMIN, MANAGER)
public record AdminRegisterRequest(LoginRequest usuario, Role role) {}