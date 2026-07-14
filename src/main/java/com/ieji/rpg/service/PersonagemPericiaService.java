package com.ieji.rpg.service;

import com.ieji.rpg.domain.dto.personagem.PersonagemPericiaRequest;
import com.ieji.rpg.domain.dto.personagem.PersonagemPericiaResponse;
import com.ieji.rpg.domain.entity.PersonagemPericia;
import com.ieji.rpg.domain.entity.PersonagemPericiaId;
import com.ieji.rpg.infra.repository.PericiaRepository;
import com.ieji.rpg.infra.repository.PersonagemPericiaRepository;
import com.ieji.rpg.infra.repository.PersonagemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class PersonagemPericiaService extends AbstractService<PersonagemPericia, PersonagemPericiaId, PersonagemPericiaRequest, PersonagemPericiaResponse> {

    @Autowired
    private PersonagemRepository personagemRepository;

    @Autowired
    private PericiaRepository periciaRepository;



    public PersonagemPericiaService(PersonagemPericiaRepository repository) {
        super(repository);
    }

    @Override
    PersonagemPericiaResponse construct(PersonagemPericiaRequest object) {
        var personagem = personagemRepository.findById(object.id().getIdPersonagem())
                .orElseThrow(() -> new EntityNotFoundException("Personagem não encontrado"));
        var pericia = periciaRepository.findById(object.id().getIdPericia())
                .orElseThrow(() -> new EntityNotFoundException("Perícia não encontrada"));

        PersonagemPericia personagemPericia = PersonagemPericia.builder()
                .id(object.id())
                .personagem(personagem)
                .pericia(pericia)
                .treinada(object.treinada())
                .build();
        repository.save(personagemPericia);

        return PersonagemPericiaResponse.constructByEntity(personagemPericia);
    }

    @Override
    protected void updateData(PersonagemPericia entity, PersonagemPericiaRequest object) {
        entity.setTreinada(object.treinada());
    }

    @Override
    protected PersonagemPericiaResponse convertToResponse(PersonagemPericia entity) {
        return PersonagemPericiaResponse.constructByEntity(entity);
    }
}