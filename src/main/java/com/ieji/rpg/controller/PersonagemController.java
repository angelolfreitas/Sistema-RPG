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

@RestController
@RequestMapping("/personagem")
@PreAuthorize("hasAuthority('user::write')")
public class PersonagemController extends AbstractController<Personagem, Integer, PersonagemRequest, PersonagemResponse> {

    protected PersonagemController(PersonagemService service) {
        super(service);
    }

    @Override
    @PostMapping
    public ResponseEntity<PersonagemResponse> create(@RequestBody PersonagemRequest dto) {
        Usuario usuario = usuarioLogado();
        PersonagemRequest seguro = new PersonagemRequest(
                dto.id(), usuario.getId(), dto.aparencia(), dto.personalidade(),
                dto.historico(), dto.objetivo(), dto.agilidade(), dto.forca(),
                dto.intelecto(), dto.presenca(), dto.vigor(), dto.nex(),
                dto.pvAtual(), dto.pvMaximo(), dto.sanAtual(), dto.sanMaxima(),
                dto.peAtual(), dto.peMaximo(), dto.defesa(), dto.nome()
        );
        return service.create(seguro)
                .map(response -> ResponseEntity.status(201).body(response))
                .orElseGet(() -> ResponseEntity.status(409).build());
    }

    private Usuario usuarioLogado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (Usuario) authentication.getPrincipal();
    }

    private boolean ehMestre(Usuario usuario) {
        return usuario.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("manager::write") || a.getAuthority().equals("admin::write"));
    }

    private boolean ehDono(PersonagemResponse personagem, Usuario usuario) {
        if (personagem.usuarioId() == null || usuario.getId() == null) {
            return false;
        }
        return personagem.usuarioId().intValue() == usuario.getId().intValue();
    }

    @Override
    @GetMapping
    public ResponseEntity<List<PersonagemResponse>> findAll() {
        Usuario usuario = usuarioLogado();

        if (ehMestre(usuario)) {
            return ResponseEntity.ok(service.findAll());
        }
        return ResponseEntity.ok(((PersonagemService) service).findByUsuarioLogado(usuario.getId()));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<PersonagemResponse> getById(@PathVariable Integer id) {
        Usuario usuario = usuarioLogado();
        PersonagemResponse personagem = service.getById(id);

        if (!ehDono(personagem, usuario) && !ehMestre(usuario)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(personagem);
    }

    @Override
    @PutMapping
    public ResponseEntity<PersonagemResponse> update(@RequestBody PersonagemRequest dto) {
        Usuario usuario = usuarioLogado();

        if (dto.id() == null) {
            return ResponseEntity.badRequest().build();
        }

        PersonagemResponse existente = service.getById(dto.id());

        if (!ehDono(existente, usuario) && !ehMestre(usuario)) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(service.update(dto));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        Usuario usuario = usuarioLogado();
        PersonagemResponse personagem = service.getById(id);

        if (!ehDono(personagem, usuario) && !ehMestre(usuario)) {
            return ResponseEntity.status(403).build();
        }

        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping("/{id}")
    public ResponseEntity<PersonagemResponse> patch(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> fields
    ) {
        Usuario usuario = usuarioLogado();
        PersonagemResponse personagem = service.getById(id);

        if (!ehDono(personagem, usuario) && !ehMestre(usuario)) {
            return ResponseEntity.status(403).build();
        }

        service.patchEntity(id, fields);
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/meu")
    public ResponseEntity<List<PersonagemResponse>> meusPersonagens() {
        Usuario usuario = usuarioLogado();
        return ResponseEntity.ok(((PersonagemService) service).findByUsuarioLogado(usuario.getId()));
    }
}