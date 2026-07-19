package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.pergunta.PerguntaRequest;
import com.ieji.rpg.domain.dto.pergunta.PerguntaResponse;
import com.ieji.rpg.domain.entity.Pergunta;
import com.ieji.rpg.service.AbstractService;
import com.ieji.rpg.service.PerguntaService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/// Essa endpoint nao está sendo utilizada por enquanto.
@RestController
@RequestMapping("/pergunta")
@PreAuthorize("hasAuthority('user::write')")
public class PerguntaController extends AbstractController<Pergunta, Integer, PerguntaRequest, PerguntaResponse> {
    protected PerguntaController(PerguntaService service) {
        super(service);
    }
}
