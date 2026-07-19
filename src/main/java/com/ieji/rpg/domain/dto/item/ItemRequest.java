package com.ieji.rpg.domain.dto.item;

import com.ieji.rpg.domain.dto.BaseDTO;
/// DTO de request.
/// precisa de um:
/// Integer: id
/// String: nome
/// String: descricao
/// Integer: quantidade
public record ItemRequest(
        Integer id,
        String nome,
        String descricao,
        Integer quantidade
) implements BaseDTO<Integer> {
    @Override public Integer getId() { return id; }
}