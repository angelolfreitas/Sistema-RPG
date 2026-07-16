package com.ieji.rpg.domain.dto.mensagem;

import com.ieji.rpg.domain.dto.BaseDTO;
import lombok.Builder;

@Builder
public record MensagemChatRequest(
        Integer id,
        Integer idCaso,
        Integer authorId,
        Integer personagemId,
        String conteudo
) implements BaseDTO<Integer> {
    @Override
    public Integer getId() {
        return id;
    }
}