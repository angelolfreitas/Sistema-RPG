package com.ieji.rpg.service;

import com.ieji.rpg.domain.dto.inventario.InventarioRequest;
import com.ieji.rpg.domain.dto.inventario.InventarioResponse;
import com.ieji.rpg.domain.entity.Inventario;
import com.ieji.rpg.domain.entity.InventarioId;
import com.ieji.rpg.infra.repository.InventarioRepository;
import com.ieji.rpg.infra.repository.ItemRepository;
import com.ieji.rpg.infra.repository.PersonagemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
@Service
public class InventarioService
        extends AbstractService<Inventario, InventarioId, InventarioRequest, InventarioResponse> {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private PersonagemRepository personagemRepository;

    public InventarioService(InventarioRepository repository) {
        super(repository);
    }

    @Override
    InventarioResponse construct(InventarioRequest object) {
        var personagem = personagemRepository.findById(object.id().getIdPersonagem())
                .orElseThrow(() -> new EntityNotFoundException("Personagem não encontrado"));

        var item = itemRepository.findById(object.id().getIdItem())
                .orElseThrow(() -> new EntityNotFoundException("Item não encontrado"));

        Inventario inventario = Inventario.builder()
                .id(object.id()) // Define a chave composta
                .personagem(personagem)
                .item(item)
                .quantidade(object.quantidade())
                .build();

        repository.save(inventario);
        return InventarioResponse.constructByEntity(inventario);
    }

    @Override
    protected void updateData(Inventario entity, InventarioRequest object) {
        // No update, geralmente só precisamos atualizar atributos simples (como a quantidade)
        entity.setQuantidade(object.quantidade());
    }

    @Override
    protected InventarioResponse convertToResponse(Inventario entity) {
        return InventarioResponse.constructByEntity(entity);
    }
}