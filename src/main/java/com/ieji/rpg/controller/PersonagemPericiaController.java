package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.personagem.PersonagemPericiaRequest;
import com.ieji.rpg.domain.dto.personagem.PersonagemPericiaResponse;
import com.ieji.rpg.domain.entity.PersonagemPericia;
import com.ieji.rpg.domain.entity.PersonagemPericiaId;
import com.ieji.rpg.service.AbstractService;
import com.ieji.rpg.service.PersonagemPericiaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/personagem-p")
@PreAuthorize("hasRole('USER')")
public class PersonagemPericiaController extends AbstractController<PersonagemPericia, PersonagemPericiaId, PersonagemPericiaRequest, PersonagemPericiaResponse> {
    protected PersonagemPericiaController(PersonagemPericiaService service) {
        super(service);
    }
}
