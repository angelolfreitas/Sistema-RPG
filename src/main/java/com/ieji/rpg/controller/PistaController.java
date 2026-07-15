package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.pista.PistaRequest;
import com.ieji.rpg.domain.dto.pista.PistaResponse;
import com.ieji.rpg.domain.entity.Pista;
import com.ieji.rpg.service.AbstractService;
import com.ieji.rpg.service.PistaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pista")
@PreAuthorize("hasRole('USER')")
public class PistaController extends AbstractController<Pista, Integer, PistaRequest, PistaResponse> {
    protected PistaController(PistaService service) {
        super(service);
    }
}
