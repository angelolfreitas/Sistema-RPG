package com.ieji.rpg.service;

import com.ieji.rpg.domain.dto.personagem.PersonagemRequest;
import com.ieji.rpg.domain.dto.personagem.PersonagemResponse;
import com.ieji.rpg.domain.entity.Personagem;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.domain.entity.role.Role;
import com.ieji.rpg.infra.repository.PersonagemRepository;
import com.ieji.rpg.infra.repository.UserRepository;
import com.ieji.rpg.infra.security.TokenService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonagemService extends AbstractService<Personagem, Integer, PersonagemRequest, PersonagemResponse> {


    private final PersonagemRepository repository;
    private final UserRepository userRepository;


    public PersonagemService(PersonagemRepository repository, UserRepository userRepository) {
        super(repository);
        this.repository = repository;
        this.userRepository = userRepository;
    }

    @Override
    protected PersonagemResponse construct(PersonagemRequest object) {
        Usuario usuario = userRepository.findById(object.usuarioId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário associado não encontrado"));
        Personagem personagem = Personagem.builder()
                .aparencia(object.aparencia())
                .usuario(usuario)
                .personalidade(object.personalidade())
                .historico(object.historico())
                .objetivo(object.objetivo())
                .agilidade(object.agilidade())
                .forca(object.forca())
                .intelecto(object.intelecto())
                .presenca(object.presenca())
                .vigor(object.vigor())
                .nex(object.nex())
                .pvAtual(object.pvAtual())
                .pvMaximo(object.pvMaximo())
                .sanAtual(object.sanAtual())
                .sanMaxima(object.sanMaxima())
                .peAtual(object.peAtual())
                .peMaximo(object.peMaximo())
                .defesa(object.defesa())
                .nomeJogador(object.nome())
                .build();

        repository.save(personagem);

        return PersonagemResponse.constructByEntity(personagem);
    }

    @Override
    protected void updateData(Personagem entity, PersonagemRequest object) {

        entity.setAparencia(object.aparencia());
        entity.setPersonalidade(object.personalidade());
        entity.setHistorico(object.historico());
        entity.setObjetivo(object.objetivo());
        entity.setAgilidade(object.agilidade());
        entity.setForca(object.forca());
        entity.setIntelecto(object.intelecto());
        entity.setPresenca(object.presenca());
        entity.setVigor(object.vigor());
        entity.setNex(object.nex());
        entity.setPvAtual(object.pvAtual());
        entity.setPvMaximo(object.pvMaximo());
        entity.setSanAtual(object.sanAtual());
        entity.setSanMaxima(object.sanMaxima());
        entity.setPeAtual(object.peAtual());
        entity.setPeMaximo(object.peMaximo());
        entity.setDefesa(object.defesa());
        entity.setNomeJogador(object.nome());
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
}
