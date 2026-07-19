package com.ieji.rpg.service;

import com.ieji.rpg.domain.dto.personagem.PersonagemRequest;
import com.ieji.rpg.domain.dto.personagem.PersonagemResponse;
import com.ieji.rpg.domain.entity.Personagem;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.infra.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PersonagemService extends AbstractService<Personagem, Integer, PersonagemRequest, PersonagemResponse> {

    private final PersonagemRepository repository;
    private final UserRepository userRepository;
    private final AutorizacaoService autorizacaoService;

    @Autowired
    private MonstroConhecidoRepository monstroConhecidoRepository;

    @Autowired
    private InventarioRepository inventarioRepository;

    @Autowired
    private PersonagemPericiaRepository personagemPericiaRepository;

    public PersonagemService(PersonagemRepository repository, UserRepository userRepository,
                             AutorizacaoService autorizacaoService) {
        super(repository);
        this.repository = repository;
        this.userRepository = userRepository;
        this.autorizacaoService = autorizacaoService;
    }

    @Override
    @Transactional
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