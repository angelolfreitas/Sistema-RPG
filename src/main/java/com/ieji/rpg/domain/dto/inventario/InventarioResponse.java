package com.ieji.rpg.domain.dto.inventario;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.Inventario;
import com.ieji.rpg.domain.entity.InventarioId;
/// DTO de response.
/// possui:
/// InventarioId: id — chave composta (idPersonagem + idItem)
/// String: nomePersonagem
/// String: nomeItem
/// Integer: quantidade
///
/// constructByEntity(): converte a entidade Inventario para este DTO,
/// mapeando id, o nomeJogador do personagem, o nome do item e a quantidade.
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