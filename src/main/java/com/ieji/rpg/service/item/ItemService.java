package com.ieji.rpg.service.item;

import com.ieji.rpg.domain.dto.item.ItemRequest;
import com.ieji.rpg.domain.dto.item.ItemResponse;
import com.ieji.rpg.domain.entity.Item;
import com.ieji.rpg.infra.repository.ItemRepository;
import com.ieji.rpg.service.AbstractService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
/// findAll, create, uodate, delete: sobrepoem com redis
///
/// construct: construct especiico da entidade.
///
/// updateData(): atuaiza os campos da entidade
///
/// converToResponse(): converte em resposta com base na aetherys
@Service
public class ItemService
extends AbstractService<Item, Integer, ItemRequest, ItemResponse> {
    public ItemService(ItemRepository repository) {
        super(repository);
    }

    @Cacheable(value = "itens", key = "'all'")
    @Override
    public List<ItemResponse> findAll() {
        return super.findAll();
    }

    @CacheEvict(value = "itens", allEntries = true)
    @Override
    public Optional<ItemResponse> create(ItemRequest dto) {
        return super.create(dto);
    }

    @CacheEvict(value = "itens", allEntries = true)
    @Override
    public ItemResponse update(ItemRequest dto) {
        return super.update(dto);
    }
    @Override
    protected ItemResponse construct(ItemRequest object) {
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
    @CacheEvict(value = "itens", allEntries = true)
    @Override
    public void delete(Integer id) {
        super.delete(id);
    }

}
