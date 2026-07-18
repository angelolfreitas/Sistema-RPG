package com.ieji.rpg.domain.dto.sessao;

import java.time.Instant;
import java.time.LocalDateTime;

public record SessaoAgendadaResponse(
        Integer id,
        Integer idCaso,
        String conteudo,
        LocalDateTime dataSessao,
        Instant criadoEm
) {}