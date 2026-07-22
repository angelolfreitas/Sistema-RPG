package com.ieji.rpg.service;

import com.ieji.rpg.domain.dto.pericia.PericiaRequest;
import com.ieji.rpg.domain.dto.pericia.PericiaResponse;
import com.ieji.rpg.domain.entity.Pericia;
import com.ieji.rpg.infra.repository.PericiaRepository;
import org.springframework.stereotype.Service;
/// Nao está sendo utilizado
@Service
public class PericiaService extends AbstractService<Pericia, Integer, PericiaRequest, PericiaResponse> {


    public PericiaService(PericiaRepository repository) {
        super(repository);
    }

    @Override
    protected PericiaResponse construct(PericiaRequest object) {
        Pericia pericia = Pericia.builder()
                .nome(object.nome())
                .atributoBase(object.atributoBase())
                .build();
        repository.save(pericia);

        return PericiaResponse.constructByEntity(pericia);
    }

    @Override
    protected void updateData(Pericia entity, PericiaRequest object) {
        entity.setNome(object.nome());
        entity.setAtributoBase(object.atributoBase());
    }

    @Override
    protected PericiaResponse convertToResponse(Pericia entity) {
        return PericiaResponse.constructByEntity(entity);
    }
}
