package com.ieji.rpg.domain.dto.sessao;

import java.time.LocalDateTime;
/// DTO de request usado para agendar uma nova sessão.
/// precisa de um:
/// String: conteudo — aviso do mestre sobre a sessão
/// LocalDateTime: dataSessao
public record AgendarSessaoRequest(String conteudo, LocalDateTime dataSessao) {}