package com.ieji.rpg.domain.dto.item;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.Item;

public record ItemResponse(
        Integer id,
        String nome,
        String descricao,
        Integer quantidade
) implements BaseDTO<Integer> {
    @Override public Integer getId() { return id; }
    public static ItemResponse constructByEntity(Item item) {
        return new ItemResponse(item.getIdItem(),
                item.getNome(),
                item.getDescricao(),
                item.getQuantidade());
    }
}