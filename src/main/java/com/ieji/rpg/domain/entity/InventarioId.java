package com.ieji.rpg.domain.entity;


import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
/// chave composta (embeddable) da relação personagem-item (inventário).
/// Possui o idPersonagem
/// o idItem
/// usada como @EmbeddedId na entidade Inventario.
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