package com.ieji.rpg.service.inventario;

import com.ieji.rpg.domain.dto.inventario.InventarioRequest;
import com.ieji.rpg.domain.dto.inventario.InventarioResponse;
import com.ieji.rpg.domain.entity.Inventario;
import com.ieji.rpg.domain.entity.InventarioId;
import com.ieji.rpg.domain.entity.Item;
import com.ieji.rpg.domain.entity.Personagem;
import com.ieji.rpg.infra.repository.InventarioRepository;
import com.ieji.rpg.infra.repository.ItemRepository;
import com.ieji.rpg.infra.repository.PersonagemRepository;
import com.ieji.rpg.service.AbstractService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
    protected InventarioResponse construct(InventarioRequest object) {
        var personagem = personagemRepository.findById(object.id().getIdPersonagem())
                .orElseThrow(() -> new EntityNotFoundException("Personagem não encontrado"));

        var item = itemRepository.findById(object.id().getIdItem())
                .orElseThrow(() -> new EntityNotFoundException("Item não encontrado"));

        Inventario inventario = Inventario.builder()
                .id(object.id())
                .personagem(personagem)
                .item(item)
                .quantidade(object.quantidade())
                .build();

        repository.save(inventario);
        return InventarioResponse.constructByEntity(inventario);
    }

    @Override
    protected void updateData(Inventario entity, InventarioRequest object) {
        entity.setQuantidade(object.quantidade());
    }

    @Override
    protected InventarioResponse convertToResponse(Inventario entity) {
        return InventarioResponse.constructByEntity(entity);
    }
    @Transactional
    @CacheEvict(value = {"inventario", "itens"}, allEntries = true)
    public Optional<Inventario> alterarQuantidade(Integer idPersonagem, Integer idItem, int delta) {
        InventarioId id = new InventarioId(idPersonagem, idItem);
        Inventario inventario = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entrada de inventário não encontrada"));

        Item item = inventario.getItem();

        if(delta > 0 && item.getQuantidade() > delta) {
            item.setQuantidade(item.getQuantidade() - delta);
            inventario.setQuantidade(inventario.getQuantidade() + delta);
        }else if(delta<0 && inventario.getQuantidade() > -delta) {
            int qtdDevolvida = Math.min(-delta, inventario.getQuantidade());
            item.setQuantidade(item.getQuantidade() + qtdDevolvida);
            itemRepository.save(item);

            int novaQuantidade = inventario.getQuantidade() - qtdDevolvida;
            if(novaQuantidade <= 0){
                repository.deleteById(id);
                return Optional.empty();
            }
            inventario.setQuantidade(novaQuantidade);
        }
        return Optional.of(repository.save(inventario));
    }

    @Transactional
    @CacheEvict(value = {"inventario", "itens"}, allEntries = true)
    public Optional<Inventario> add(Integer idPersonagem, Integer idItem, Integer quantity) {

        if (idPersonagem == null || idItem == null || quantity == null) {
            throw new IllegalArgumentException("personagemId, idItem e quantidade são obrigatórios");
        }

        Item item = itemRepository.findByIdWithLock(idItem)
                .orElseThrow(() -> new EntityNotFoundException("Item não encontrado"));

        Personagem personagem = personagemRepository.findById(idPersonagem)
                .orElseThrow(() -> new EntityNotFoundException("Personagem não encontrado"));


        item.removerEstoque(quantity);
        itemRepository.save(item);

        InventarioId invId = new InventarioId(idPersonagem, idItem);
        Optional<Inventario> existente = repository.findById(invId);

        Inventario inventario;
        if (existente.isPresent()) {
            inventario = existente.get();
            inventario.setQuantidade(inventario.getQuantidade() + quantity);
        } else {
            inventario = new Inventario();
            inventario.setId(invId);
            inventario.setPersonagem(personagem);
            inventario.setItem(item);
            inventario.setQuantidade(quantity);
        }

        return Optional.of(repository.save(inventario));
    }

    @Transactional
    @CacheEvict(value = {"inventario", "itens"}, allEntries = true)
        public void remove(Integer idPersonagem, Integer idItem){

        InventarioId inventarioId = new InventarioId(idPersonagem, idItem);
        Inventario inventario =  repository.findById(inventarioId)
                .orElseThrow(() -> new EntityNotFoundException("Entrada de inventário não encontrada"));

        Item item = inventario.getItem();
        item.setQuantidade(item.getQuantidade() + inventario.getQuantidade());
        itemRepository.save(item);

        repository.deleteById(inventarioId);
    }

    @Cacheable(value = "inventario", key = "#usuarioId")
    public List<InventarioResponse> listarPorUsuario(Integer usuarioId) {
        return ((InventarioRepository) repository)
                .findByPersonagem_Usuario_Id(usuarioId)
                .stream()
                .map(InventarioResponse::constructByEntity)
                .toList();
    }
}