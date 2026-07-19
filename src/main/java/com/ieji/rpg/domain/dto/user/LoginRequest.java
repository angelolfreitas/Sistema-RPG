package com.ieji.rpg.domain.dto.user;

import com.ieji.rpg.domain.dto.BaseDTO;
/// DTO de request usado tanto para login quanto para cadastro/atualização de usuário.
/// precisa de um:
/// String: username
/// String: login — usado como e-mail do usuário
/// String: password
///
/// getId(): retorna sempre 0, pois este DTO não representa uma entidade
/// já persistida com id próprio (usado apenas para autenticação/criação).
public record LoginRequest(String username, String login, String password) implements BaseDTO<Integer> {
    @Override
    public Integer getId() {
        return 0;
    }
}