package com.ieji.rpg.domain.dto.password;

public record ResetPasswordRequest(String token, String novaSenha) {}