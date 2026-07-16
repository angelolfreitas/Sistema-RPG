package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.mensagem.MensagemChatRequest;
import com.ieji.rpg.domain.dto.mensagem.MensagemChatResponse;
import com.ieji.rpg.domain.entity.MensagemChat;
import com.ieji.rpg.service.MensagemChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@PreAuthorize("hasAuthority('user::read')")
public class MensagemChatController extends AbstractController<MensagemChat, Integer, MensagemChatRequest, MensagemChatResponse> {

    protected MensagemChatController(MensagemChatService service) {
        super(service);
    }

    // Endpoint customizado para pegar o histórico inteiro de uma sessão específica
    @GetMapping("/caso/{idCaso}")
    public ResponseEntity<List<MensagemChatResponse>> getHistorico(@PathVariable Integer idCaso) {
        List<MensagemChatResponse> historico = ((MensagemChatService) service).listarHistoricoDoCaso(idCaso);
        return ResponseEntity.ok(historico);
    }
}