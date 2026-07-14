package com.ieji.rpg.domain.dto.caso;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.CasoInvestigacao;

public record CasoResponse(
        Integer id,
        String nomeCaso,
        String resumo,
        String objetivo,
        String urgencia,
        Integer rodadasRestantes
) implements BaseDTO<Integer> {
    @Override public Integer getId() { return id; }

    public static CasoResponse constructByEntity(CasoInvestigacao casoInvestigacao) {
        return new CasoResponse(casoInvestigacao.getIdCaso(),
                casoInvestigacao.getNomeCaso(),
                casoInvestigacao.getResumo(),
                casoInvestigacao.getObjetivo(),
                casoInvestigacao.getUrgencia(),
                casoInvestigacao.getRodadasRestantes());
    }
}