package com.ieji.rpg.domain.dto.monstro;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.Monstro;

public record MonstroResponse(
        Integer id,
        String nome,
        Integer pv,
        String san,
        String ataquesEspeciais,
        String comportamento,
        String fraquezas
) implements BaseDTO<Integer> {
    @Override public Integer getId() { return id; }
    public static MonstroResponse constructByEntity(Monstro monstro) {
        return new MonstroResponse(monstro.getIdMonstro(),
                monstro.getNome(),
                monstro.getPv(),
                monstro.getSan(),
                monstro.getAtaquesEspeciais(),
                monstro.getComportamento(),
                monstro.getFraquezas());
    }
}