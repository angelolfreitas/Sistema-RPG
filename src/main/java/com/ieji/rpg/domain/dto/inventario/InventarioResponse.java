package com.ieji.rpg.domain.dto.inventario;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.Inventario;
import com.ieji.rpg.domain.entity.InventarioId;

public record InventarioResponse(
        InventarioId id,
        String nomePersonagem,
        String nomeItem,
        Integer quantidade
) implements BaseDTO<InventarioId> {
    @Override public InventarioId getId() { return id; }

    public static InventarioResponse constructByEntity(Inventario inventario) {
        return new InventarioResponse(inventario.getId(),
                inventario.getPersonagem().getNomeJogador(),
                inventario.getItem().getNome(),
                inventario.getQuantidade());
    }
}