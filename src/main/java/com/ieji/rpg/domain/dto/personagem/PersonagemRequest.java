package com.ieji.rpg.domain.dto.personagem;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.Personagem;
import com.ieji.rpg.domain.entity.Usuario;

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
    public static PersonagemRequest constructByEntity(PersonagemRequest object)
    {
        return constructByEntity(object, object.usuarioId());
    }
    public static PersonagemRequest constructByEntity(PersonagemRequest personagem, Integer usuarioId) {
        return  new PersonagemRequest(
                personagem.getId(),
                usuarioId,
                personagem.aparencia(),
                personagem.personalidade(),
                personagem.historico(),
                personagem.objetivo(),
                personagem.agilidade(),
                personagem.forca(),
                personagem.intelecto(),
                personagem.presenca(),
                personagem.vigor(),
                personagem.nex(),
                personagem.pvAtual(),
                personagem.pvMaximo(),
                personagem.sanAtual(),
                personagem.sanMaxima(),
                personagem.peAtual(),
                personagem.peMaximo(),
                personagem.defesa(),
                personagem.nome()
        );
    }
}