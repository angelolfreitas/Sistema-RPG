package com.ieji.rpg.domain.dto.caso;

import com.ieji.rpg.domain.dto.BaseDTO;
public record CasoRequest(
        Integer id,
        String nomeCaso,
        String resumo,
        String objetivo,
        String urgencia,
        Integer rodadasRestantes
) implements BaseDTO<Integer> {
    @Override public Integer getId() { return id; }
}