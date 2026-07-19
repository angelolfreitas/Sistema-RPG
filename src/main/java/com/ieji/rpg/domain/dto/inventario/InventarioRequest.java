package com.ieji.rpg.domain.dto.inventario;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.InventarioId;
/// DTO de request.
/// precisa de um:
/// InventarioId: id — chave composta (idPersonagem + idItem)
/// Integer: personagemId
/// Integer: quantidade
public record InventarioRequest(
        InventarioId id,
        Integer personagemId,
        Integer quantidade
) implements BaseDTO<InventarioId> {
    @Override public InventarioId getId() { return id; }
}