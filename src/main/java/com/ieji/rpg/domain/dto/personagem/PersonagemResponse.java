package com.ieji.rpg.domain.dto.personagem;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.Personagem;

public record PersonagemResponse(
        Integer id,
        String nomeJogador,
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
        String token
) implements BaseDTO<Integer> {
    @Override public Integer getId() { return id; }
    public static PersonagemResponse constructByEntity(Personagem personagem) {
        return constructByEntity(personagem, null);
    }
    public static PersonagemResponse constructByEntity(Personagem personagem, String token) {
        return  new PersonagemResponse(
                personagem.getIdPersonagem(),
                personagem.getNomeJogador(),
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
                token
        );
    }
}