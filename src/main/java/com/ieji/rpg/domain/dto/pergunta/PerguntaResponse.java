package com.ieji.rpg.domain.dto.pergunta;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.Pergunta;
/// DTO de response.
/// possui:
/// Integer: id — id da pergunta
/// Integer: idCaso
/// String: textoPergunta
///
/// constructByEntity(): converte a entidade Pergunta para este DTO,
/// mapeando idPergunta, o idCaso (do caso associado) e textoPergunta.
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