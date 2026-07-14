package com.ieji.rpg.domain.dto.personagem;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.PersonagemPericia;
import com.ieji.rpg.domain.entity.PersonagemPericiaId;

public record PersonagemPericiaResponse(
        PersonagemPericiaId id,
        String nomePericia,
        Boolean treinada
) implements BaseDTO<PersonagemPericiaId> {
    @Override public PersonagemPericiaId getId() { return id; }
    public static PersonagemPericiaResponse constructByEntity(PersonagemPericia personagemPericia) {
        return new PersonagemPericiaResponse(
                personagemPericia.getId(),
                personagemPericia.getPericia().getNome(),
                personagemPericia.getTreinada()
        );
    }
}