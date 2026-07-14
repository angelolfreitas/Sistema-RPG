package com.ieji.rpg.domain.dto.personagem;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.PersonagemPericiaId;

public record PersonagemPericiaRequest(
        PersonagemPericiaId id,
        Boolean treinada
) implements BaseDTO<PersonagemPericiaId> {
    @Override public PersonagemPericiaId getId() { return id; }
}