package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.personagem.PersonagemRequest;
import com.ieji.rpg.domain.dto.personagem.PersonagemResponse;
import com.ieji.rpg.domain.entity.Personagem;
import com.ieji.rpg.service.AbstractService;
import com.ieji.rpg.service.PersonagemService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/personagem")
@PreAuthorize("hasRole('USER')")
public class PersonagemController extends AbstractController<Personagem, Integer, PersonagemRequest, PersonagemResponse> {
    protected PersonagemController(PersonagemService service) {
        super(service);
    }
}
