package com.ieji.rpg.service;

import com.ieji.rpg.domain.dto.pista.PistaRequest;
import com.ieji.rpg.domain.dto.pista.PistaResponse;
import com.ieji.rpg.domain.entity.Pista;
import com.ieji.rpg.infra.repository.CasoInvestigacaoRepository;
import com.ieji.rpg.infra.repository.PistaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
/// Nao está sendo utilizado
@Service
public class PistaService extends AbstractService<Pista, Integer, PistaRequest, PistaResponse> {

    private final CasoInvestigacaoRepository casoRepository;

    public PistaService(PistaRepository repository, CasoInvestigacaoRepository casoRepository) {
        super(repository);
        this.casoRepository = casoRepository;
    }

    @Override
    protected PistaResponse construct(PistaRequest object) {
        var caso = casoRepository.findById(object.idCaso())
                .orElseThrow(() -> new EntityNotFoundException("Caso não encontrado"));

        Pista pista = Pista.builder()
                .caso(caso)
                .descricao(object.descricao())
                .tipo(object.tipo())
                .descoberta(object.descoberta())
                .build();
        repository.save(pista);

        return PistaResponse.constructByEntity(pista);
    }

    @Override
    protected void updateData(Pista entity, PistaRequest object) {
        var caso = casoRepository.findById(object.idCaso())
                .orElseThrow(() -> new EntityNotFoundException("Caso não encontrado"));

        entity.setCaso(caso);
        entity.setDescricao(object.descricao());
        entity.setTipo(object.tipo());
        entity.setDescoberta(object.descoberta());
    }

    @Override
    protected PistaResponse convertToResponse(Pista entity) {
        return PistaResponse.constructByEntity(entity);
    }
}