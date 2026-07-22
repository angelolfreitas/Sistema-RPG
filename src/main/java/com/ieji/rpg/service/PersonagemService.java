package com.ieji.rpg.service;

import com.ieji.rpg.domain.dto.personagem.PersonagemRequest;
import com.ieji.rpg.domain.dto.personagem.PersonagemResponse;
import com.ieji.rpg.domain.entity.Personagem;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.infra.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
/// delete(): sobrescreve o delete() padrão com limpeza em cascata manual.
/// Antes de apagar o personagem, remove seus registros de inventário,
/// suas perícias (personagemPericia) e os monstros conhecidos por ele.
/// Também limpa a coleção de aetherys associada (se o personagem existir),
/// para evitar problemas de referência, e só então chama o delete() genérico
/// do AbstractService para remover o personagem em si.
///
/// construct(): cria um novo personagem a partir do DTO.
/// Busca o usuário dono do personagem pelo id informado (lança exceção se
/// não existir), monta a entidade Personagem associada a esse usuário,
/// salva no repositório e retorna a resposta convertida.
///
/// updateData(): delega a atualização dos campos do personagem para o
/// próprio método setByEntity() da entidade, repassando o DTO recebido.
///
/// convertToResponse(): converte a entidade Personagem para o DTO de resposta.
///
/// findByUsuarioLogado(): lista todos os personagens pertencentes a um usuário,
/// já convertidos para o DTO de resposta.
///
/// ehMestre(): delega para o AutorizacaoService a verificação se o usuário
/// possui papel de mestre.
///
/// ehDono(): verifica se o usuário informado é o dono do personagem,
/// comparando os ids (com proteção contra nulos).
///
/// getComAcesso(): busca o personagem pelo id e valida a permissão de acesso:
/// só permite se o usuário for o dono do personagem ou for mestre; caso
/// contrário, lança AccessDeniedException (convertida pelo Spring Security em 403).
///
/// listarParaUsuario(): se o usuário for mestre, retorna todos os personagens
/// do sistema; caso contrário, retorna apenas os personagens do próprio usuário.
///
/// updateComAcesso(): valida o acesso do usuário ao personagem (via getComAcesso())
/// antes de delegar a atualização para o update() padrão.
///
/// deleteComAcesso(): valida o acesso do usuário ao personagem antes de
/// delegar a exclusão para o delete() (com toda a limpeza em cascata).
///
/// patchComAcesso(): valida o acesso do usuário ao personagem, aplica o patch
/// parcial dos campos informados e retorna o personagem atualizado.
@Service
@Transactional
public class PersonagemService extends AbstractService<Personagem, Integer, PersonagemRequest, PersonagemResponse> {

    private final PersonagemRepository repository;
    private final UserRepository userRepository;
    private final AutorizacaoService autorizacaoService;

    private final MonstroConhecidoRepository monstroConhecidoRepository;
    private final InventarioRepository inventarioRepository;

    private final PersonagemPericiaRepository personagemPericiaRepository;

    public PersonagemService(PersonagemRepository repository, UserRepository userRepository,
                             AutorizacaoService autorizacaoService, MonstroConhecidoRepository monstroConhecidoRepository, InventarioRepository inventarioRepository, PersonagemPericiaRepository personagemPericiaRepository) {
        super(repository);
        this.repository = repository;
        this.userRepository = userRepository;
        this.autorizacaoService = autorizacaoService;
        this.monstroConhecidoRepository = monstroConhecidoRepository;
        this.inventarioRepository = inventarioRepository;
        this.personagemPericiaRepository = personagemPericiaRepository;
    }

    @Override
    public void delete(Integer id) {
        inventarioRepository.deleteByPersonagem_IdPersonagem(id);
        personagemPericiaRepository.deleteByPersonagem_IdPersonagem(id);
        monstroConhecidoRepository.deleteByPersonagem_IdPersonagem(id); // já existia
        repository.findById(id).ifPresent(p -> p.getAetherys().clear());
        super.delete(id);
    }

    @Override
    protected PersonagemResponse construct(PersonagemRequest object) {
        Usuario usuario = userRepository.findById(object.usuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário associado não encontrado"));
        Personagem personagem = Personagem.consturctPersonagem(object, usuario);

        repository.save(personagem);

        return PersonagemResponse.constructByEntity(personagem);
    }

    @Override
    protected void updateData(Personagem entity, PersonagemRequest object) {
        entity.setByEntity(object);
    }

    @Override
    protected PersonagemResponse convertToResponse(Personagem entity) {
        return PersonagemResponse.constructByEntity(entity);
    }

    public List<PersonagemResponse> findByUsuarioLogado(Integer usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream()
                .map(PersonagemResponse::constructByEntity)
                .toList();
    }

    public boolean ehMestre(Usuario usuario) {
        return autorizacaoService.ehMestre(usuario);
    }

    public boolean ehDono(PersonagemResponse personagem, Usuario usuario) {
        if (personagem.usuarioId() == null || usuario.getId() == null) {
            return false;
        }
        return personagem.usuarioId().intValue() == usuario.getId().intValue();
    }

    /**
     * Retorna o personagem se o usuário for dono ou mestre; caso contrário
     * lança AccessDeniedException, que o Spring Security já converte em 403
     * (mesmo mecanismo usado pelo @PreAuthorize).
     */
    public PersonagemResponse getComAcesso(Integer id, Usuario usuario) {
        PersonagemResponse personagem = getById(id);
        if (!ehDono(personagem, usuario) && !ehMestre(usuario)) {
            throw new AccessDeniedException("Sem permissão para acessar este personagem");
        }
        return personagem;
    }

    public List<PersonagemResponse> listarParaUsuario(Usuario usuario) {
        return ehMestre(usuario) ? findAll() : findByUsuarioLogado(usuario.getId());
    }

    public PersonagemResponse updateComAcesso(PersonagemRequest dto, Usuario usuario) {
        getComAcesso(dto.id(), usuario);
        return update(dto);
    }

    public void deleteComAcesso(Integer id, Usuario usuario) {
        getComAcesso(id, usuario);
        delete(id);
    }

    public PersonagemResponse patchComAcesso(Integer id, Map<String, Object> fields, Usuario usuario) {
        getComAcesso(id, usuario);
        patchEntity(id, fields);
        return getById(id);
    }
}