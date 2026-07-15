package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.monstro.MonstroRequest;
import com.ieji.rpg.domain.dto.monstro.MonstroResponse;
import com.ieji.rpg.domain.entity.Monstro;
import com.ieji.rpg.service.AbstractService;
import com.ieji.rpg.service.MonstroService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/monstro")
@PreAuthorize("hasRole('USER')")
public class MonstroController extends AbstractController<Monstro, Integer, MonstroRequest, MonstroResponse> {
    protected MonstroController(MonstroService service) {
        super(service);
    }
}
