package com.ieji.rpg.domain.dto.pista;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.Pista;
/// DTO de response.
/// possui:
/// Integer: id — id da pista
/// Integer: idCaso
/// String: descricao
/// String: tipo
/// Boolean: descoberta
///
/// constructByEntity(): converte a entidade Pista para este DTO,
/// mapeando idPista, o idCaso (do caso associado), descricao, tipo e descoberta.
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