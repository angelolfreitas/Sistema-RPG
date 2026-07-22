package com.ieji.rpg.domain.dto.mensagem;

import com.ieji.rpg.domain.entity.MensagemChat;
import lombok.Builder;

import java.time.Instant;

/// DTO de response.
/// possui:
/// Integer: id — id da mensagem
/// Integer: idCaso
/// String: autorNome — nome de exibição já resolvido (ex.: "nomeJogador - username")
/// String: conteudo
/// LocalDateTime: enviadoEm
///
/// constructByEntity(): converte a entidade MensagemChat para este DTO,
/// mapeando id, idCaso (do caso associado), nomeExibicao (como autorNome),
/// conteudo e enviadoEm.
@Builder
public record MensagemChatResponse(
        Integer id,
        Integer idCaso,
        String autorNome,
        String conteudo,
        Instant enviadoEm
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