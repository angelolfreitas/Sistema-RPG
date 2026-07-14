package com.ieji.rpg.service;

import com.ieji.rpg.domain.dto.pergunta.PerguntaRequest;
import com.ieji.rpg.domain.dto.pergunta.PerguntaResponse;
import com.ieji.rpg.domain.entity.Pergunta;
import com.ieji.rpg.infra.repository.CasoInvestigacaoRepository;
import com.ieji.rpg.infra.repository.PerguntaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
public class PerguntaService extends AbstractService<Pergunta, Integer, PerguntaRequest, PerguntaResponse> {
    @Autowired
    private CasoInvestigacaoRepository casoRepository;

    public PerguntaService(PerguntaRepository repository) {
        super(repository);
    }

    @Override
    PerguntaResponse construct(PerguntaRequest object) {
        var caso = casoRepository.findById(object.idCaso())
                .orElseThrow(() -> new EntityNotFoundException("Caso não encontrado"));

        Pergunta pergunta = Pergunta.builder()
                .caso(caso)
                .textoPergunta(object.textoPergunta())
                .build();
        repository.save(pergunta);

        return PerguntaResponse.constructByEntity(pergunta);
    }

    @Override
    protected void updateData(Pergunta entity, PerguntaRequest object) {
        var caso = casoRepository.findById(object.idCaso())
                .orElseThrow(() -> new EntityNotFoundException("Caso não encontrado"));

        entity.setCaso(caso);
        entity.setTextoPergunta(object.textoPergunta());
    }

    @Override
    protected PerguntaResponse convertToResponse(Pergunta entity) {
        return PerguntaResponse.constructByEntity(entity);
    }
}