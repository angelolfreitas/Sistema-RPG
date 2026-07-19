package com.ieji.rpg.domain.dto.sessao;

import java.time.Instant;
import java.time.LocalDateTime;
/// DTO de response.
/// possui:
/// Integer: id — id da sessão agendada
/// Integer: idCaso
/// String: conteudo
/// LocalDateTime: dataSessao
/// Instant: criadoEm
public record SessaoAgendadaResponse(
        Integer id,
        Integer idCaso,
        String conteudo,
        LocalDateTime dataSessao,
        Instant criadoEm
) {}