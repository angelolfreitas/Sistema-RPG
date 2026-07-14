package com.ieji.rpg.domain.dto.inventario;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.InventarioId;

public record InventarioRequest(
        InventarioId id,
        Integer personagemId,
        Integer quantidade
) implements BaseDTO<InventarioId> {
    @Override public InventarioId getId() { return id; }
}