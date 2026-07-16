package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.inventario.InventarioResponse;
import com.ieji.rpg.domain.dto.monstro.MonstroRequest;
import com.ieji.rpg.domain.dto.monstro.MonstroResponse;
import com.ieji.rpg.domain.entity.Monstro;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.service.MonstroService;
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
}