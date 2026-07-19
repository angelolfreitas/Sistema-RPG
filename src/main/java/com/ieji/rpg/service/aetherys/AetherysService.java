package com.ieji.rpg.service.aetherys;

import com.ieji.rpg.domain.dto.aetherys.AetherysRequest;
import com.ieji.rpg.domain.dto.aetherys.AetherysResponse;
import com.ieji.rpg.domain.entity.Aetherys;
import com.ieji.rpg.infra.repository.AetherysRepository;
import com.ieji.rpg.service.AbstractService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
/// findAll, create, uodate, delete: sobrepoem com redis
///
/// construct: construct especiico da entidade.
///
/// updateData(): atuaiza os campos da entidade
///
/// converToResponse(): converte em resposta com base na aetherys
@Service
public class AetherysService extends AbstractService<Aetherys, Integer, AetherysRequest, AetherysResponse> {
    AetherysService(AetherysRepository aetherysRepository) {
        super(aetherysRepository);
    }
    @Cacheable(value = "aetherys", key = "'all'")
    @Override
    public List<AetherysResponse> findAll() {
        return super.findAll();
    }

    @CacheEvict(value = "aetherys", allEntries = true)
    @Override
    public Optional<AetherysResponse> create(AetherysRequest dto) {
        return super.create(dto);
    }

    @CacheEvict(value = "aetherys", allEntries = true)
    @Override
    public AetherysResponse update(AetherysRequest dto) {
        return super.update(dto);
    }

    @CacheEvict(value = "aetherys", allEntries = true)
    @Override
    public void delete(Integer id) {
        super.delete(id);
    }
    @Override
    protected AetherysResponse construct(AetherysRequest object) {

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
