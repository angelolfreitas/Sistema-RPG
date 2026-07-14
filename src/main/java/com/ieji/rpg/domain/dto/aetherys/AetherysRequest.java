package com.ieji.rpg.domain.dto.aetherys;

import com.ieji.rpg.domain.dto.BaseDTO;

public record AetherysRequest(
        Integer id,
        String nome,
        String funcao,
        String testeExigido
) implements BaseDTO<Integer> {
    @Override public Integer getId() { return id; }
}