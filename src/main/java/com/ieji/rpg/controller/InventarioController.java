package com.ieji.rpg.controller;

import com.ieji.rpg.domain.Exception.EstoqueInsuficienteException;
import com.ieji.rpg.domain.dto.inventario.InventarioRequest;
import com.ieji.rpg.domain.dto.inventario.InventarioResponse;
import com.ieji.rpg.domain.dto.personagem.PersonagemResponse;
import com.ieji.rpg.domain.entity.Inventario;
import com.ieji.rpg.domain.entity.InventarioId;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.service.InventarioService;
import com.ieji.rpg.service.PersonagemService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/inventario")
@PreAuthorize("hasAuthority('user::write')")
public class InventarioController extends AbstractController<Inventario, InventarioId, InventarioRequest, InventarioResponse> {

    @Autowired
    private PersonagemService personagemService;

    protected InventarioController(InventarioService service) {
        super(service);
    }

    private boolean ehMestre(Usuario usuario) {
        return usuario.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("manager::write") || a.getAuthority().equals("admin::write"));
    }

    // dono é resolvido a partir do usuário logado; se ele não tem esse personagem, ou não é mestre, 403
    private boolean podeMexerNoPersonagem(Integer idPersonagem, Usuario usuario) {
        if (ehMestre(usuario)) return true;
        PersonagemResponse personagem = personagemService.getById(idPersonagem);
        return personagem.usuarioId() != null && personagem.usuarioId().equals(usuario.getId());
    }

    // Endpoint novo: cada usuário só vê o próprio inventário
    @GetMapping("/meu")
    public ResponseEntity<List<InventarioResponse>> meuInventario(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(((InventarioService) service).listarPorUsuario(usuario.getId()));
    }

    // GET geral (herdado) fica restrito a mestres — jogador comum não deveria bater aqui
    @Override
    @GetMapping
    @PreAuthorize("hasAuthority('manager::write') or hasAuthority('admin::write')")
    public ResponseEntity<List<InventarioResponse>> findAll() {
        return super.findAll();
    }
    @Override
    @PostMapping
    public ResponseEntity<InventarioResponse> create(@RequestBody InventarioRequest inventarioRequest) {
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        try {
            Integer idPersonagem = inventarioRequest.personagemId();

            if (!podeMexerNoPersonagem(idPersonagem, usuarioLogado)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            Inventario inventario = ((InventarioService) service).add(
                    idPersonagem, inventarioRequest.getId().getIdItem(), inventarioRequest.quantidade()
            ).orElseThrow(() -> new EntityNotFoundException("nao achou"));
            return ResponseEntity.ok(InventarioResponse.constructByEntity(inventario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new InventarioResponse(null, null, null, null));
        } catch (EstoqueInsuficienteException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new InventarioResponse(null, null, null, null));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new InventarioResponse(null, null, null, null));
        }
    }

    @DeleteMapping("/{idPersonagem}/{idItem}")
    public ResponseEntity<?> remover(@PathVariable Integer idPersonagem, @PathVariable Integer idItem,
                                     @AuthenticationPrincipal Usuario usuarioLogado) {
        if (!podeMexerNoPersonagem(idPersonagem, usuarioLogado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ((InventarioService) service).remove(idPersonagem, idItem);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{idPersonagem}/{idItem}")
    public ResponseEntity<?> alterarQuantidade(
            @PathVariable Integer idPersonagem,
            @PathVariable Integer idItem,
            @RequestParam Integer delta,
            @AuthenticationPrincipal Usuario usuarioLogado) {
        if (!podeMexerNoPersonagem(idPersonagem, usuarioLogado)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        try {
            Optional<Inventario> resultado = ((InventarioService) service).alterarQuantidade(idPersonagem, idItem, delta);
            if (resultado.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(InventarioResponse.constructByEntity(resultado.get()));
        } catch (EstoqueInsuficienteException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("conflict", e.getMessage()));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("not found", e.getMessage()));
        }
    }
}