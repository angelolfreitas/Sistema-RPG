package com.ieji.rpg.domain.dto.personagem;

import com.ieji.rpg.domain.dto.BaseDTO;

public record PersonagemRequest(
        Integer id,
        Integer usuarioId,
        String aparencia,
        String personalidade,
        String historico,
        String objetivo,
        Integer agilidade,
        Integer forca,
        Integer intelecto,
        Integer presenca,
        Integer vigor,
        Integer nex,
        Integer pvAtual,
        Integer pvMaximo,
        Integer sanAtual,
        Integer sanMaxima,
        Integer peAtual,
        Integer peMaximo,
        Integer defesa,
        String nome
) implements BaseDTO<Integer> {
    @Override public Integer getId() { return id; }
}