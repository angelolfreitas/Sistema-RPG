package com.ieji.rpg.service;

import com.ieji.rpg.domain.dto.aetherys.AetherysRequest;
import com.ieji.rpg.domain.dto.aetherys.AetherysResponse;
import com.ieji.rpg.domain.entity.Aetherys;
import com.ieji.rpg.infra.repository.AetherysRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;
@Service
public class AetherysService extends AbstractService<Aetherys, Integer, AetherysRequest, AetherysResponse>{
    AetherysService(AetherysRepository aetherysRepository) {
        super(aetherysRepository);
    }
    @Override
    AetherysResponse construct(AetherysRequest object) {

        Aetherys aetherys = Aetherys.builder()
                .nome(object.nome())
                .funcao(object.funcao())
                .testeExigido(object.testeExigido())
                .build();
        repository.save(aetherys);


        return AetherysResponse.constructByEntity(aetherys);
    }

    @Override
    protected void updateData(Aetherys entity, AetherysRequest object) {
        entity.setNome(object.nome());
        entity.setFuncao(object.funcao());
        entity.setTesteExigido(object.testeExigido());
    }

    @Override
    protected AetherysResponse convertToResponse(Aetherys entity) {
        return AetherysResponse.constructByEntity(entity);
    }


}
