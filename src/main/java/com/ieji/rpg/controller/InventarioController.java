package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.inventario.InventarioRequest;
import com.ieji.rpg.domain.dto.inventario.InventarioResponse;
import com.ieji.rpg.domain.entity.Inventario;
import com.ieji.rpg.domain.entity.InventarioId;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.service.PersonagemService;
import com.ieji.rpg.service.inventario.InventarioService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/// meuInventario(): lista o inventario do personagem logado.
/// findAll(): restrito a mestre/admin.
/// create()/remover()/alterarQuantidade(): a checagem de posse do personagem
/// (dono ou mestre) é delegada a PersonagemService.getComAcesso, a mesma
/// regra usada em PersonagemController — se negar, lança AccessDeniedException
/// e o Spring Security já devolve 403 sozinho.
@RestController
@RequestMapping("/inventario")
@PreAuthorize("hasAuthority('user::write')")
public class InventarioController extends AbstractController<Inventario, InventarioId, InventarioRequest, InventarioResponse> {

    private final PersonagemService personagemService;
    private final InventarioService inventarioService;

    protected InventarioController(InventarioService service, PersonagemService personagemService) {
        super(service);
        this.personagemService = personagemService;
        this.inventarioService = service;
    }

    @GetMapping("/meu")
    public ResponseEntity<List<InventarioResponse>> meuInventario(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(inventarioService.listarPorUsuario(usuario.getId()));
    }

    @Override
    @GetMapping
    @PreAuthorize("hasAuthority('manager::write') or hasAuthority('admin::write')")
    public ResponseEntity<List<InventarioResponse>> findAll() {
        return super.findAll();
    }

    @Override
    @PostMapping
    public ResponseEntity<InventarioResponse> create(@RequestBody InventarioRequest dto) {
        Integer idPersonagem = dto.personagemId();
        personagemService.getComAcesso(idPersonagem, usuarioLogado());

        Inventario inventario = inventarioService
                .add(idPersonagem, dto.getId().getIdItem(), dto.quantidade())
                .orElseThrow(() -> new EntityNotFoundException("Item não encontrado"));
        return ResponseEntity.ok(InventarioResponse.constructByEntity(inventario));
    }

    @DeleteMapping("/{idPersonagem}/{idItem}")
    public ResponseEntity<Void> remover(@PathVariable Integer idPersonagem, @PathVariable Integer idItem,
                                        @AuthenticationPrincipal Usuario usuario) {
        personagemService.getComAcesso(idPersonagem, usuario);
        inventarioService.remove(idPersonagem, idItem);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{idPersonagem}/{idItem}")
    public ResponseEntity<InventarioResponse> alterarQuantidade(
            @PathVariable Integer idPersonagem,
            @PathVariable Integer idItem,
            @RequestParam Integer delta,
            @AuthenticationPrincipal Usuario usuario) {
        personagemService.getComAcesso(idPersonagem, usuario);

        Optional<Inventario> resultado = inventarioService.alterarQuantidade(idPersonagem, idItem, delta);
        return resultado
                .map(inventario -> ResponseEntity.ok(InventarioResponse.constructByEntity(inventario)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }


    private Usuario usuarioLogado() {
        return (Usuario) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
    }
}