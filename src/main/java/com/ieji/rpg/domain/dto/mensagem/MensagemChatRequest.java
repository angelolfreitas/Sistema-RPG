package com.ieji.rpg.domain.dto.mensagem;

import com.ieji.rpg.domain.dto.BaseDTO;
import lombok.Builder;
/// DTO de request.
/// precisa de um:
/// Integer: id
/// Integer: idCaso
/// Integer: authorId — usado quando não há usuário autenticado no contexto (fluxo alternativo)
/// Integer: personagemId — opcional, personagem que está "falando" na mensagem
/// String: conteudo
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