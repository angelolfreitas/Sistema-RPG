package com.ieji.rpg.domain.dto.pergunta;

import com.ieji.rpg.domain.dto.BaseDTO;

public record PerguntaRequest(
        Integer id,
        Integer idCaso,
        String textoPergunta
) implements BaseDTO<Integer> {
    @Override public Integer getId() { return id; }
}