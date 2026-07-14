package com.ieji.rpg.domain.dto.pericia;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.Pericia;

public record PericiaResponse(
        Integer id,
        String nome,
        String atributoBase
) implements BaseDTO<Integer> {
    @Override public Integer getId() { return id; }
    public static PericiaResponse constructByEntity(Pericia pericia) {
        return new PericiaResponse(
                pericia.getIdPericia(),
                pericia.getNome(),
                pericia.getAtributoBase()
        );
    }
}