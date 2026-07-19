package com.ieji.rpg.domain.dto.aetherys;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.Aetherys;
/// DTO de response.
/// possui:
/// Integer: id — id do aetherys
/// String: nome
/// String: funcao
/// String: testeExigido
///
/// constructByEntity(): converte a entidade Aetherys para este DTO,
/// mapeando idAetherys, nome, funcao e testeExigido.
public record AetherysResponse(
        Integer id,
        String nome,
        String funcao,
        String testeExigido
) implements BaseDTO<Integer> {
    @Override public Integer getId() { return id; }

    public static AetherysResponse constructByEntity(Aetherys aetherys) {
        return new AetherysResponse(aetherys.getIdAetherys(),
                aetherys.getNome(),
                aetherys.getFuncao(),
                aetherys.getTesteExigido());
    }
}