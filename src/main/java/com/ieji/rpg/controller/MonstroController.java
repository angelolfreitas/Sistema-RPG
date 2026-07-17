package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.monstro.MonstroRequest;
import com.ieji.rpg.domain.dto.monstro.MonstroResponse;
import com.ieji.rpg.domain.entity.monstro.Monstro;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.service.monstro.MonstroService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/monstro")
@PreAuthorize("hasAuthority('user::write')")
public class MonstroController extends AbstractController<Monstro, Integer, MonstroRequest, MonstroResponse> {

    protected MonstroController(MonstroService service) {
        super(service);
    }

    @Override
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('admin::write')")
    public ResponseEntity<MonstroResponse> patch(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> fields
    ) {
        service.patchEntity(id, fields);
        return ResponseEntity.ok(service.getById(id));
    }

    @Override
    @GetMapping
    public ResponseEntity<List<MonstroResponse>> findAll() {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(((MonstroService) service).listarParaUsuario(usuario));
    }
    @PreAuthorize("hasAuthority('admin::read')")
    @Override
    public ResponseEntity<MonstroResponse> getById(Integer integer) {
        return super.getById(integer);
    }
    @PreAuthorize("hasAuthority('admin::write')")
    @Override
    public ResponseEntity<MonstroResponse> create(@RequestBody MonstroRequest dto) {
        return super.create(dto);
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


    @PostMapping("/{id}/dano")
    @PreAuthorize("hasAuthority('admin::write')")
    public ResponseEntity<MonstroResponse> aplicarDano(
            @PathVariable Integer id,
            @RequestBody Map<String, Integer> body
    ) {
        Integer dano = body.get("dano");
        return ResponseEntity.ok(((MonstroService) service).aplicarDano(id, dano));
    }
}