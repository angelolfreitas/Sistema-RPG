package com.ieji.rpg.domain.dto.pergunta;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.Pergunta;

public record PerguntaResponse(
        Integer id,
        Integer idCaso,
        String textoPergunta
) implements BaseDTO<Integer> {
    @Override public Integer getId() { return id; }
    public static PerguntaResponse constructByEntity(Pergunta pergunta) {
        return new PerguntaResponse(
                pergunta.getIdPergunta(),
                pergunta.getCaso().getIdCaso(),
                pergunta.getTextoPergunta()
        );
    }
}