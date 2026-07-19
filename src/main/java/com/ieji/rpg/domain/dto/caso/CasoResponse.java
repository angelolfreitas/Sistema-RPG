package com.ieji.rpg.domain.dto.caso;

import com.ieji.rpg.domain.dto.BaseDTO;
import com.ieji.rpg.domain.entity.CasoInvestigacao;
/// DTO de response.
/// possui:
/// Integer: id — id do caso
/// String: nomeCaso
/// String: resumo
/// String: objetivo
/// String: urgencia
/// Integer: rodadasRestantes
///
/// constructByEntity(): converte a entidade CasoInvestigacao para este DTO,
/// mapeando idCaso, nomeCaso, resumo, objetivo, urgencia e rodadasRestantes.
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