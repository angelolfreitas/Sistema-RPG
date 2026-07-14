package com.ieji.rpg.domain.dto.item;

import com.ieji.rpg.domain.dto.BaseDTO;

public record ItemRequest(
        Integer id,
        String nome,
        String descricao,
        Integer quantidade
) implements BaseDTO<Integer> {
    @Override public Integer getId() { return id; }
}