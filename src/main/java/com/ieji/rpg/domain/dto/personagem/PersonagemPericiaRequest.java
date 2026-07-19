package com.ieji.rpg.domain.dto.personagem;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.PersonagemPericiaId;
/// DTO de request.
/// precisa de um:
/// PersonagemPericiaId: id — chave composta (idPersonagem + idPericia)
/// Boolean: treinada — se o personagem é treinado nessa perícia
public record PersonagemPericiaRequest(
        PersonagemPericiaId id,
        Boolean treinada
) implements BaseDTO<PersonagemPericiaId> {
    @Override public PersonagemPericiaId getId() { return id; }
}