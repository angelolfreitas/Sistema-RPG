package com.ieji.rpg.service;

import com.ieji.rpg.domain.dto.caso.CasoRequest;
import com.ieji.rpg.domain.dto.caso.CasoResponse;
import com.ieji.rpg.domain.entity.CasoInvestigacao;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.infra.repository.CasoInvestigacaoRepository;
import com.ieji.rpg.infra.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CasoInvestigacaoService extends AbstractService<CasoInvestigacao, Integer, CasoRequest, CasoResponse> {

    private final UserRepository usuarioRepository;

    public CasoInvestigacaoService(CasoInvestigacaoRepository repository, UserRepository usuarioRepository) {
        super(repository);
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected CasoResponse construct(CasoRequest dto) {
        Usuario usuarioLogado = (Usuario) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        Usuario mestre = usuarioRepository.findByEmail(usuarioLogado.getEmail())
                .orElseThrow(() -> new RuntimeException("Usuário mestre não encontrado"));

        CasoInvestigacao caso = CasoInvestigacao.builder()
                .nomeCaso(dto.nomeCaso())
                .resumo(dto.resumo())
                .objetivo(dto.objetivo())
                .urgencia(dto.urgencia())
                .rodadasRestantes(dto.rodadasRestantes())
                .status(CasoInvestigacao.StatusCaso.ABERTA)
                .mestre(mestre)
                .build();

        CasoInvestigacao savedCaso = repository.save(caso);
        return convertToResponse(savedCaso);
    }

    @Override
    protected void updateData(CasoInvestigacao entity, CasoRequest   dto) {
        if (dto.nomeCaso() != null) entity.setNomeCaso(dto.nomeCaso());
        if (dto.resumo() != null) entity.setResumo(dto.resumo());
        if (dto.objetivo() != null) entity.setObjetivo(dto.objetivo());
        if (dto.urgencia() != null) entity.setUrgencia(dto.urgencia());
        if (dto.rodadasRestantes() != null) entity.setRodadasRestantes(dto.rodadasRestantes());
    }

    @Override
    protected CasoResponse convertToResponse(CasoInvestigacao entity) {
        return CasoResponse.constructByEntity(entity);
    }

    @Transactional
    public void adicionarJogador(Integer casoId, String emailJogador) {
        CasoInvestigacao caso = repository.findById(casoId)
                .orElseThrow(() -> new RuntimeException("Caso não encontrado"));
        Usuario jogador = usuarioRepository.findByEmail(emailJogador)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        caso.getJogadores().add(jogador);
        repository.save(caso);
    }

    // Você pode criar um UsuarioResponse ou retornar um Map, deixei genérico
    public List<String> listarJogadores(Integer casoId) {
        CasoInvestigacao caso = repository.findById(casoId)
                .orElseThrow(() -> new RuntimeException("Caso não encontrado"));

        return caso.getJogadores().stream()
                .map(Usuario::getUsername)
                .toList();
    }
}