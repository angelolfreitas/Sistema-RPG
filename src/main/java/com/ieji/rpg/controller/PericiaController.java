package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.pericia.PericiaRequest;
import com.ieji.rpg.domain.dto.pericia.PericiaResponse;
import com.ieji.rpg.domain.entity.Pericia;
import com.ieji.rpg.service.PericiaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/// A entidade desse controller nao estásendo usada por enquanto
@RestController
@RequestMapping("/pericia")
@PreAuthorize("hasAuthority('user::write')")
public class PericiaController extends AbstractController<Pericia, Integer, PericiaRequest, PericiaResponse> {
    protected PericiaController(PericiaService service) {
        super(service);
    }
}
