package com.ieji.rpg.domain.dto.sessao;

import java.time.LocalDateTime;

public record AgendarSessaoRequest(String conteudo, LocalDateTime dataSessao) {}