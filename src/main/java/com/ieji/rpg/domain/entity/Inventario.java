package com.ieji.rpg.domain.entity;

import jakarta.persistence.*;
import lombok.*;
/// tabela de inventário, associando itens a personagens.
/// Possui o id composto (InventarioId: idPersonagem + idItem)
/// o personagem dono (mapeado via @MapsId em idPersonagem, lazy)
/// o item guardado (mapeado via @MapsId em idItem, lazy)
/// a quantidade do item que o personagem possui (default 1)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idPersonagem")
    @JoinColumn(name = "id_personagem")
    private Personagem personagem;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idItem")
    @JoinColumn(name = "id_item")
    private Item item;

    @Column(nullable = false)
    private Integer quantidade = 1;
}
