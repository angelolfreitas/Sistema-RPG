package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.caso.CasoRequest;
import com.ieji.rpg.domain.dto.caso.CasoResponse;
import com.ieji.rpg.domain.dto.caso.CasoUsuarioResponse;
import com.ieji.rpg.domain.dto.sessao.AgendarSessaoRequest;
import com.ieji.rpg.domain.dto.sessao.SessaoAgendadaResponse;
import com.ieji.rpg.domain.entity.CasoInvestigacao;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.service.CasoInvestigacaoService;
import com.ieji.rpg.service.SessaoAgendadaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

///Controller de casos de investigação. Tem:
///  post que precisa de um CasoRequest
///  update, que precisa do mesmo corpo para criar
/// delete, que precisa do id
/// sem necessidade de patch por hora
///
/// agendarSessao(): a partir de um AgendarSessaoRequest e de um id de sessao, agenda a sessao.
/// Ver mais sobre agendamentos de sessao na classe service de agendamentos.
///
/// listarSessoesAgendaas(): mostra as sessoes agendadas do banco. aparece na tela de usuarios
///
/// entrarNaSessao(): a partir do token e d oemail do suaurio 9que é único no banco), agenda uma sessao para
/// dado usuario
///
/// listarJogadores(): lsita os jogadores da sessao
///
///
/// listarUsuariosCompletos(): lsita o objeto todo
///
/// cancelarSessao(): a partir de um id e de uma sessao, cancela.
@RestController
@RequestMapping("/casos")
public class CasoController extends AbstractController<CasoInvestigacao, Integer, CasoRequest, CasoResponse> {

    private final SessaoAgendadaService sessaoAgendadaService;
    private final CasoInvestigacaoService casoInvestigacaoService;

    protected CasoController(CasoInvestigacaoService service, SessaoAgendadaService sessaoAgendadaService) {
        super(service);
        this.sessaoAgendadaService = sessaoAgendadaService;
        this.casoInvestigacaoService = service;
    }
    @Override
    @PostMapping
    @PreAuthorize("hasAuthority('manager::write') or hasAuthority('admin::write')")
    public ResponseEntity<CasoResponse> create(@RequestBody CasoRequest dto) {
        return super.create(dto);
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



    @PostMapping("/{id}/sessoes")
    @PreAuthorize("hasAuthority('manager::write') or hasAuthority('admin::write')")
    public ResponseEntity<SessaoAgendadaResponse> agendarSessao(@PathVariable Integer id,
                                                                @RequestBody AgendarSessaoRequest request) {
        return ResponseEntity.ok(sessaoAgendadaService.agendar(id, request));
    }

    @GetMapping("/{id}/sessoes")
    @PreAuthorize("hasAuthority('user::read')")
    public ResponseEntity<List<SessaoAgendadaResponse>> listarSessoesAgendadas(@PathVariable Integer id) {
        return ResponseEntity.ok(sessaoAgendadaService.listar(id));
    }


    @PostMapping("/{id}/entrar")
    @PreAuthorize("hasAuthority('user::write')")
    public ResponseEntity<Void> entrarNaSessao(@PathVariable Integer id, Authentication auth) {
        Usuario usuarioLogado = (Usuario) auth.getPrincipal();
        casoInvestigacaoService.adicionarJogador(id, Objects.requireNonNull(usuarioLogado).getEmail());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/jogadores")
    @PreAuthorize("hasAuthority('user::read')")
    public ResponseEntity<List<String>> listarJogadores(@PathVariable Integer id) {
        List<String> jogadores = casoInvestigacaoService.listarJogadores(id);
        return ResponseEntity.ok(jogadores);
    }
    @GetMapping("/{id}/usuarios")
    @PreAuthorize("hasAuthority('admin::write') or hasAuthority('manager::write')")
    public ResponseEntity<List<CasoUsuarioResponse>> listarUsuariosCompletos(@PathVariable Integer id) {
        return ResponseEntity.ok(casoInvestigacaoService.listarUsuariosCompletos(id));
    }


    @DeleteMapping("/{id}/sessoes/{idSessao}")
    @PreAuthorize("hasAuthority('manager::write') or hasAuthority('admin::write')")
    public ResponseEntity<Void> cancelarSessao(@PathVariable Integer id, @PathVariable Integer idSessao) {
        sessaoAgendadaService.cancelar(id, idSessao);
        return ResponseEntity.noContent().build();
    }
}