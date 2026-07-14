package com.ieji.rpg.domain.entity;


import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
public class PersonagemPericiaId implements Serializable {
    private Integer idPersonagem;
    private Integer idPericia;
}