package com.ieji.rpg.domain.entity;


import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
/// chave composta (embeddable) da relação personagem-perícia.
/// Possui o idPersonagem
/// o idPericia
/// usada como @EmbeddedId na entidade PersonagemPericia.
@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class PersonagemPericiaId implements Serializable {
    private Integer idPersonagem;
    private Integer idPericia;
}