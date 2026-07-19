package com.ieji.rpg.domain.dto.personagem;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.PersonagemPericia;
import com.ieji.rpg.domain.entity.PersonagemPericiaId;
/// DTO de response.
/// possui:
/// PersonagemPericiaId: id — chave composta (idPersonagem + idPericia)
/// String: nomePericia
/// Boolean: treinada
///
/// constructByEntity(): converte a entidade PersonagemPericia para este DTO,
/// mapeando id, o nome da pericia associada e treinada.
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