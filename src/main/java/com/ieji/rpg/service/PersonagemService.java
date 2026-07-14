package com.ieji.rpg.service;

import com.ieji.rpg.domain.dto.personagem.PersonagemRequest;
import com.ieji.rpg.domain.dto.personagem.PersonagemResponse;
import com.ieji.rpg.domain.entity.Personagem;
import com.ieji.rpg.infra.repository.PersonagemRepository;
import com.ieji.rpg.infra.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PersonagemService extends AbstractService<Personagem, Integer, PersonagemRequest, PersonagemResponse> {


    private final PersonagemRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    public PersonagemService(PersonagemRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    PersonagemResponse construct(PersonagemRequest object) {
        Personagem personagem = Personagem.builder()
                .nomeJogador(object.nomeJogador())
                .aparencia(object.aparencia())
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
                .password(object.password())
                .build();
        String token = this.tokenService.generateToken(personagem);
        repository.save(personagem);

        return PersonagemResponse.constructByEntity(personagem, token);
    }

    @Override
    protected void updateData(Personagem entity, PersonagemRequest object) {
        entity.setNomeJogador(object.nomeJogador());
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
    }

    @Override
    protected PersonagemResponse convertToResponse(Personagem entity) {
        return PersonagemResponse.constructByEntity(entity);
    }
}
