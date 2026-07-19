package com.ieji.rpg.domain.dto.password;
/// DTO de request usado para iniciar o fluxo de "esqueci minha senha".
/// precisa de um:
/// String: email — e-mail do usuário que solicitou o reset
public record ForgotPasswordRequest(String email) {}