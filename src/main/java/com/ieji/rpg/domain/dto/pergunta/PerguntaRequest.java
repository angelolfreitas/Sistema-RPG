package com.ieji.rpg.domain.dto.pergunta;

import com.ieji.rpg.domain.dto.BaseDTO;
/// DTO de request.
/// precisa de um:
/// Integer: id
/// Integer: idCaso
/// String: textoPergunta
public record PerguntaRequest(
        Integer id,
        Integer idCaso,
        String textoPergunta
) implements BaseDTO<Integer> {
    @Override public Integer getId() { return id; }
}