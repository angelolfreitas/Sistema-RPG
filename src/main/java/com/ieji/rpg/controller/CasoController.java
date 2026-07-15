package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.caso.CasoRequest;
import com.ieji.rpg.domain.dto.caso.CasoResponse;
import com.ieji.rpg.domain.entity.CasoInvestigacao;
import com.ieji.rpg.infra.repository.CasoInvestigacaoRepository;
import com.ieji.rpg.service.AbstractService;
import com.ieji.rpg.service.CasoInvestigacaoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/caso")
@PreAuthorize("hasRole('USER')")
public class CasoController  extends AbstractController<CasoInvestigacao, Integer, CasoRequest, CasoResponse>
{
    public CasoController(CasoInvestigacaoService service) {
        super(service);
    }
}
