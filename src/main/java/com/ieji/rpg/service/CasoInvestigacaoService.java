package com.ieji.rpg.service;

import com.ieji.rpg.domain.dto.caso.CasoRequest;
import com.ieji.rpg.domain.dto.caso.CasoResponse;
import com.ieji.rpg.domain.dto.caso.CasoUsuarioResponse;
import com.ieji.rpg.domain.entity.CasoInvestigacao;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.infra.repository.CasoInvestigacaoRepository;
import com.ieji.rpg.infra.repository.MensagemChatRepository;
import com.ieji.rpg.infra.repository.SessaoAgendadaRepository;
import com.ieji.rpg.infra.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
///
/// listarUsuariosCompletos(): lista todos os usuários dentro de uma investigação pelo dto
///
/// construct(): Recupera o usuário lgoado pelo principal (token)
/// recupera o usuário mestre da sessão
///
/// Cria um novo caso de investigação. Considerando que é somente mestre, passa este usuário como mestre da sessão e
/// salva no inventário. COnverte para DTO.
///
/// adicionarJogador(): Recupera o caso pelo id e o jogador (agente) pelo email. Adiciona o jogador à lista de jogadores.
/// Salva o caso no repositório
///
/// listarJogadores(): procura o caso pelo id, s enao achar, lança exceção.
/// Cria uma resposta com base nas entidades achadas por mapeamento de streams (só msotra os nomes)
///
/// delete(): Regra complexa
///
/// Procura o caso. Se não achar, lança exceção (criar depois)
///
/// deleta todas as mensagens do caso de investigação.
/// deleta todas as sessões agendadas.
///  limpa os jgoadores associados ao caso a ser deletado
/// salva o caso no banco
///
///
///
@Service
public class CasoInvestigacaoService extends AbstractService<CasoInvestigacao, Integer, CasoRequest, CasoResponse> {

    private final UserRepository usuarioRepository;

    private final MensagemChatRepository mensagemChatRepository;


    private final SessaoAgendadaRepository sessaoAgendadaRepository;

    public CasoInvestigacaoService(CasoInvestigacaoRepository repository, UserRepository usuarioRepository, MensagemChatRepository mensagemChatRepository, SessaoAgendadaRepository sessaoAgendadaRepository) {
        super(repository);
        this.usuarioRepository = usuarioRepository;
        this.mensagemChatRepository = mensagemChatRepository;
        this.sessaoAgendadaRepository = sessaoAgendadaRepository;
    }
    @Transactional
    public List<CasoUsuarioResponse> listarUsuariosCompletos(Integer casoId) {
        CasoInvestigacao caso = repository.findById(casoId)
                .orElseThrow(() -> new EntityNotFoundException("Caso não encontrado"));
        return caso.getJogadores().stream()
                .map(u -> new CasoUsuarioResponse(u.getId(), u.getUsername(), u.getRole().name()))
                .toList();
    }

    @Override
    protected CasoResponse construct(CasoRequest dto) {
        Usuario usuarioLogado = (Usuario) Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getPrincipal();

        Usuario mestre = usuarioRepository.findByEmail(usuarioLogado.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Usuário mestre não encontrado"));

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
                .orElseThrow(() -> new EntityNotFoundException("Caso não encontrado"));
        Usuario jogador = usuarioRepository.findByEmail(emailJogador)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        caso.getJogadores().add(jogador);
        repository.save(caso);
    }

    // Você pode criar um UsuarioResponse ou retornar um Map, deixei genérico
    public List<String> listarJogadores(Integer casoId) {
        CasoInvestigacao caso = repository.findById(casoId)
                .orElseThrow(() -> new EntityNotFoundException("Caso não encontrado"));

        return caso.getJogadores().stream()
                .map(Usuario::getUsername)
                .toList();
    }


    @Override
    @Transactional
    public void delete(Integer id) {
        CasoInvestigacao caso = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Caso não encontrado"));

        mensagemChatRepository.deleteByCasoIdCaso(id);
        sessaoAgendadaRepository.deleteByCasoIdCaso(id);

        caso.getJogadores().clear();
        repository.save(caso);

        super.delete(id);
    }
}