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
/// Service de inventario.
///
///
/// O construct() é responsável por salvar no repository.
/// Ele acha o id do personagem selecionado pelo jogador. Acha os itens selecionados.
/// Constrói um novo objeto de inventário e associa o item ao personagem.
///
///
/// updateData(): recorre à função setQuantidade. atualiza a quantidade de itens.
///
/// alterarQuantidade(): regra de negocio complexa para itens que já estão no inventário:
///
/// Recupera a chave compsota inventario id, que tem o id do personagem e o id do item (varios personagems varios itens)
///
/// Encontra a isntância de inventário ja associada ao personagem (pois vamos alterar uma já existente)
///
/// Recupera o item guardado no inventário.
///
/// Se a quandiade for de acréscimo e houverem itens no estoque:
///  subtrai a quantidade de itens disponíveis e soma eles ao personagem
/// Se não, se a quantidade for d descréscimo e a quantiade disponível no usuário for maior que a quantidade subtraída:
///  subtrai do personagem e soma aos itens
///  Se a quantidade nova for menor que 0, deleta ele do usuário automaticamente
/// Se não, não faz nada (pode lançar uma exceção personalizada aqui)
///
/// Por fim, salva o novo estado no banco e retorna.
///
/// add(): novo item no inventário, regra complexa:
///
/// se quaisquer das requsicieos necessárais para criar novo inventário sao invállidas, nova exceção (criar uma específica)
/// Procura o item do repositório co lock (para evitar concorrencia)
/// Procura o personagem requisitado
///
/// remove o item do estoque
/// salva a nova quantidade d eitens
///
/// cria uma nova chave composta, declara uma variavel de inventario.
/// Se o inventario apra este item já existe: Simplesmetne altera  quantidade
///
/// Se não, cria um novo invetnário come ssa chave composta, o id do personagem, o id do item e a quantidade
///
/// Por fim, salva o inventário no repositório
///
/// remove(): regra complexa
/// Procura a chave composta. Procura o inventário (se nao existir, lança exceção - fazer )
///
/// Pega o item encapsulado pelo inventário
/// muda a quantidade do item no estoque, adicionando toda a quantidade do personagem
///
/// salva o item no banco
///
/// deleta o inventário do personagem.
///
/// listarPorUsuario(): lista os itens de cad ausuário de forma dinâmica
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


}