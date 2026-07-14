package com.ieji.rpg.service;

import com.ieji.rpg.domain.dto.caso.CasoRequest;
import com.ieji.rpg.domain.dto.caso.CasoResponse;
import com.ieji.rpg.domain.entity.CasoInvestigacao;
import com.ieji.rpg.infra.repository.CasoInvestigacaoRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class CasoInvestigacaoService extends AbstractService<CasoInvestigacao, Integer, CasoRequest, CasoResponse>
{
    public CasoInvestigacaoService(CasoInvestigacaoRepository repository) {
        super(repository);
    }

    @Override
    CasoResponse construct(CasoRequest object) {
        CasoInvestigacao caso = CasoInvestigacao.builder()
                .nomeCaso(object.nomeCaso())
                .objetivo(object.objetivo())
                .resumo(object.resumo())
                .urgencia(object.urgencia())
                .rodadasRestantes(object.rodadasRestantes())
                .build();

        repository.save(caso);
        return CasoResponse.constructByEntity(caso);
    }

    @Override
    protected void updateData(CasoInvestigacao entity, CasoRequest object) {
        entity.setNomeCaso(object.nomeCaso());
        entity.setObjetivo(object.objetivo());
        entity.setResumo(object.resumo());
        entity.setUrgencia(object.urgencia());
        entity.setRodadasRestantes(object.rodadasRestantes());
    }

    @Override
    protected CasoResponse convertToResponse(CasoInvestigacao entity) {
        return CasoResponse.constructByEntity(entity);
    }
}
