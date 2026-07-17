package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.caso.CasoRequest;
import com.ieji.rpg.domain.dto.caso.CasoResponse;
import com.ieji.rpg.domain.dto.caso.CasoUsuarioResponse;
import com.ieji.rpg.domain.entity.CasoInvestigacao;
import com.ieji.rpg.service.CasoInvestigacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/casos")
public class CasoController extends AbstractController<CasoInvestigacao, Integer, CasoRequest, CasoResponse> {

    protected CasoController(CasoInvestigacaoService service) {
        super(service);
    }

    @Override
    @PostMapping
    @PreAuthorize("hasAuthority('manager::write') or hasAuthority('admin::write')")
    public ResponseEntity<CasoResponse> create(@RequestBody CasoRequest dto) {
        return super.create(dto);
    }

    @PostMapping("/{id}/entrar")
    @PreAuthorize("hasAuthority('user::write') or hasAuthority('manager::write')")
    public ResponseEntity<Void> entrarNaSessao(@PathVariable Integer id, Authentication auth) {
        ((CasoInvestigacaoService) service).adicionarJogador(id, auth.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/jogadores")
    @PreAuthorize("hasAuthority('user::read') or hasAuthority('manager::read')")
    public ResponseEntity<List<String>> listarJogadores(@PathVariable Integer id) {
        List<String> jogadores = ((CasoInvestigacaoService) service).listarJogadores(id);
        return ResponseEntity.ok(jogadores);
    }
    @GetMapping("/{id}/usuarios")
    @PreAuthorize("hasAuthority('admin::write') or hasAuthority('manager::write')")
    public ResponseEntity<List<CasoUsuarioResponse>> listarUsuariosCompletos(@PathVariable Integer id) {
        return ResponseEntity.ok(((CasoInvestigacaoService) service).listarUsuariosCompletos(id));
    }

    @Override
    @PutMapping
    @PreAuthorize("hasAuthority('manager::write') or hasAuthority('admin::write')")
    public ResponseEntity<CasoResponse> update(@RequestBody CasoRequest dto) {
        return super.update(dto);
    }
    @Override
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin::write')") // Apenas admin pode deletar (como no Aetherys)
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        return super.delete(id);
    }
}