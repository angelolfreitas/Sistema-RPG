package com.ieji.rpg.domain.dto.mensagem;

import com.ieji.rpg.domain.entity.MensagemChat;
import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record MensagemChatResponse(
        Integer id,
        Integer idCaso,
        String autorNome,
        String conteudo,
        LocalDateTime enviadoEm
) {
    public static MensagemChatResponse constructByEntity(MensagemChat entity) {
        return MensagemChatResponse.builder()
                .id(entity.getId())
                .idCaso(entity.getCaso().getIdCaso())
                .autorNome(entity.getNomeExibicao())
                .conteudo(entity.getConteudo())
                .enviadoEm(entity.getEnviadoEm())
                .build();
    }
}