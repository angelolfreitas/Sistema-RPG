package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.monstro.MonstroRequest;
import com.ieji.rpg.domain.dto.monstro.MonstroResponse;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.domain.entity.monstro.Monstro;
import com.ieji.rpg.service.monstro.MonstroService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/// Como monstro teme relacoes complexas, tivemos ue alterar muito o crud básico.
///
/// Diferente do crud basico, tem o metodo findAll. Monstros tem regras diferentes para
/// admins, managers e users. Verificar no service as regras.
///
/// O método aplicarDano recebe um id e um corpo de patch.
/// Pelas regras do service, reduz o campo pv do monstro.
@RestController
@RequestMapping("/monstro")
@PreAuthorize("hasAuthority('user::write')")
public class MonstroController extends AbstractController<Monstro, Integer, MonstroRequest, MonstroResponse> {

    private final MonstroService monstroService;

    protected MonstroController(MonstroService service) {
        super(service);
        this.monstroService = service;
    }
    @Override
    @GetMapping
    public ResponseEntity<List<MonstroResponse>> findAll() {
        Usuario usuario = (Usuario) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
        return ResponseEntity.ok((monstroService.listarParaUsuario(usuario)));
    }
    @PostMapping("/{id}/dano")
    @PreAuthorize("hasAuthority('admin::write')")
    public ResponseEntity<MonstroResponse> aplicarDano(
            @PathVariable Integer id,
            @RequestBody Map<String, Integer> body
    ) {
        Integer dano = body.get("dano");
        return ResponseEntity.ok((monstroService.aplicarDano(id, dano)));
    }

    @Override
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('admin::write')")
    public ResponseEntity<MonstroResponse> patch(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> fields
    ) {
        return super.patch(id, fields);
    }

    @PreAuthorize("hasAuthority('manager::write')")
    @Override
    public ResponseEntity<MonstroResponse> create(@RequestBody MonstroRequest dto) {
        return super.create(dto);
    }


    @PreAuthorize("hasAuthority('admin::read')")
    @Override
    public ResponseEntity<MonstroResponse> getById(Integer integer) {
        return super.getById(integer);
    }


    @PreAuthorize("hasAuthority('admin::write')")
    @Override
    public ResponseEntity<MonstroResponse> update(@RequestBody MonstroRequest dto) {
        return super.update(dto);
    }

    @PreAuthorize("hasAuthority('admin::write')")
    @Override
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        return super.delete(id);
    }



}