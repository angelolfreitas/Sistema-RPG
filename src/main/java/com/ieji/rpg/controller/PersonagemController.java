package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.personagem.PersonagemRequest;
import com.ieji.rpg.domain.dto.personagem.PersonagemResponse;
import com.ieji.rpg.domain.entity.Personagem;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.service.PersonagemService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/personagem")
@PreAuthorize("hasAuthority('user::write')")
public class PersonagemController extends AbstractController<Personagem, Integer, PersonagemRequest, PersonagemResponse> {
    private final PersonagemService personagemService;

    protected PersonagemController(PersonagemService service) {
        super(service);
        this.personagemService = service;
    }

    @Override
    @PostMapping
    public ResponseEntity<PersonagemResponse> create(@RequestBody PersonagemRequest dto) {
        PersonagemRequest seguro = PersonagemRequest.constructByEntity(dto, usuarioLogado().getId());
        return service.create(seguro)
                .map(response -> ResponseEntity.status(201).body(response))
                .orElseGet(() -> ResponseEntity.status(409).build());
    }

    @Override
    @GetMapping
    public ResponseEntity<List<PersonagemResponse>> findAll() {
        return ResponseEntity.ok(personagemService.listarParaUsuario(usuarioLogado()));
    }

    @GetMapping("/meu")
    public ResponseEntity<List<PersonagemResponse>> meusPersonagens() {
        return ResponseEntity.ok(personagemService.findByUsuarioLogado(usuarioLogado().getId()));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<PersonagemResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(personagemService.getComAcesso(id, usuarioLogado()));
    }

    @Override
    @PutMapping
    public ResponseEntity<PersonagemResponse> update(@RequestBody PersonagemRequest dto) {
        if (dto.id() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(personagemService.updateComAcesso(dto, usuarioLogado()));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        personagemService.deleteComAcesso(id, usuarioLogado());
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping("/{id}")
    public ResponseEntity<PersonagemResponse> patch(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> fields
    ) {
        return ResponseEntity.ok(personagemService.patchComAcesso(id, fields, usuarioLogado()));
    }



    private Usuario usuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) Objects.requireNonNull(authentication).getPrincipal();
    }
}