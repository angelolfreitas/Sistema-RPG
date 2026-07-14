package com.ieji.rpg.domain.dto.pericia;

import com.ieji.rpg.domain.dto.BaseDTO;

public record PericiaRequest(
        Integer id,
        String nome,
        String atributoBase
) implements BaseDTO<Integer> {
    @Override public Integer getId() { return id; }
}