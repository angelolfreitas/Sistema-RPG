package com.ieji.rpg.domain.dto.password;
/// DTO de request usado para efetivar a redefinição de senha.
/// precisa de um:
/// String: token — token de reset recebido por e-mail
/// String: novaSenha
public record ResetPasswordRequest(String token, String novaSenha) {}