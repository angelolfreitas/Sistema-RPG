package com.ieji.rpg.domain.dto.pista;

import com.ieji.rpg.domain.dto.BaseDTO;

public record PistaRequest(
        Integer id,
        Integer idCaso,
        String descricao,
        String tipo,
        Boolean descoberta
) implements BaseDTO<Integer> {
    @Override public Integer getId() { return id; }
}