package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.mensagem.MensagemChatRequest;
import com.ieji.rpg.domain.dto.mensagem.MensagemChatResponse;
import com.ieji.rpg.domain.entity.MensagemChat;
import com.ieji.rpg.service.MensagemChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/// Aproveita as authorities do suer e dos métodos da superclasse.
///
/// Possui o método getttorico, que retorna o historico de mensagens dos casos
@RestController
@RequestMapping("/chat")
@PreAuthorize("hasAuthority('user::read')")
public class MensagemChatController extends AbstractController<MensagemChat, Integer, MensagemChatRequest, MensagemChatResponse> {
    private final MensagemChatService mensagemChatService;

    protected MensagemChatController(MensagemChatService service) {
        super(service);
        this.mensagemChatService = service;
    }

    @GetMapping("/caso/{idCaso}")
    public ResponseEntity<List<MensagemChatResponse>> getHistorico(@PathVariable Integer idCaso) {
        List<MensagemChatResponse> historico = mensagemChatService.listarHistoricoDoCaso(idCaso);
        return ResponseEntity.ok(historico);
    }
}