package com.ieji.rpg.domain.entity;


import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class InventarioId implements Serializable {
    private Integer idPersonagem;
    private Integer idItem;
}