package com.ieji.rpg.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inventario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventario {

    @EmbeddedId
    private InventarioId id = new InventarioId();

    @ManyToOne
    @MapsId("idPersonagem")
    @JoinColumn(name = "id_personagem")
    private Personagem personagem;

    @ManyToOne
    @MapsId("idItem")
    @JoinColumn(name = "id_item")
    private Item item;

    @Column(nullable = false)
    private Integer quantidade = 1;
}
