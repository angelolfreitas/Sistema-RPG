package com.ieji.rpg.domain.dto.caso;
/// DTO simples de resposta, usado para exibir um jogador vinculado a um caso.
/// possui:
/// Integer: id — id do usuário
/// String: username
/// String: role — papel do usuário (ex.: USER, ADMIN)
public record CasoUsuarioResponse(Integer id, String username, String role) {}