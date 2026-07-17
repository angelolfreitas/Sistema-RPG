package com.ieji.rpg.domain.dto.monstro;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.monstro.MaterialMonstro;

public record MonstroRequest(
        Integer id,
        String nome,
        Integer pv,
        Integer pvMaximo,
        String san,
        String ataquesEspeciais,
        String comportamento,
        String fraquezas,
        String imagemUrl,
        Boolean conhecido,
        MaterialMonstro material
) implements BaseDTO<Integer> {
    @Override public Integer getId() { return id; }
}