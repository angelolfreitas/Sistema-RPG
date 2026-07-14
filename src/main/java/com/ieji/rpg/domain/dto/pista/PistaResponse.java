package com.ieji.rpg.domain.dto.pista;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.Pista;

public record PistaResponse(
        Integer id,
        Integer idCaso,
        String descricao,
        String tipo,
        Boolean descoberta
) implements BaseDTO<Integer> {
    @Override public Integer getId() { return id; }
    public static PistaResponse constructByEntity(Pista pista) {
        return new PistaResponse(
                pista.getIdPista(),
                pista.getCaso().getIdCaso(),
                pista.getDescricao(),
                pista.getTipo(),
                pista.getDescoberta()
        );
    }
}