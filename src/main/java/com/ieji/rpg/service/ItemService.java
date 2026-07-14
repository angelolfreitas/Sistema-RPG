package com.ieji.rpg.service;

import com.ieji.rpg.domain.dto.item.ItemRequest;
import com.ieji.rpg.domain.dto.item.ItemResponse;
import com.ieji.rpg.domain.entity.Item;
import com.ieji.rpg.infra.repository.ItemRepository;
import org.springframework.stereotype.Service;

@Service
public class ItemService
extends AbstractService<Item, Integer, ItemRequest, ItemResponse> {
    public ItemService(ItemRepository repository) {
        super(repository);
    }

    @Override
    ItemResponse construct(ItemRequest object) {
        Item item = Item.builder()
                .quantidade(object.quantidade())
                .descricao(object.descricao())
                .nome(object.nome())
                .build();
        repository.save(item);

        return ItemResponse.constructByEntity(item);
    }

    @Override
    protected void updateData(Item entity, ItemRequest object) {
        entity.setQuantidade(object.quantidade());
        entity.setDescricao(object.descricao());
        entity.setNome(object.nome());
    }

    @Override
    protected ItemResponse convertToResponse(Item entity) {
        return ItemResponse.constructByEntity(entity);
    }
}
