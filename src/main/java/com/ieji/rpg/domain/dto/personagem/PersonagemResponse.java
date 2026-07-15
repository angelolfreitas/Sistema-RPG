package com.ieji.rpg.domain.dto.personagem;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.Personagem;

public record PersonagemResponse(
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
    public static PersonagemResponse constructByEntity(Personagem personagem) {
        return  new PersonagemResponse(
                personagem.getIdPersonagem(),
                personagem.getUsuario().getId(),
                personagem.getAparencia(),
                personagem.getPersonalidade(),
                personagem.getHistorico(),
                personagem.getObjetivo(),
                personagem.getAgilidade(),
                personagem.getForca(),
                personagem.getIntelecto(),
                personagem.getPresenca(),
                personagem.getVigor(),
                personagem.getNex(),
                personagem.getPvAtual(),
                personagem.getPvMaximo(),
                personagem.getSanAtual(),
                personagem.getSanMaxima(),
                personagem.getPeAtual(),
                personagem.getPeMaximo(),
                personagem.getDefesa(),
                personagem.getNomeJogador()
        );
    }
}