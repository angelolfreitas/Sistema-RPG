package com.ieji.rpg.service;

import com.ieji.rpg.domain.dto.mensagem.MensagemChatRequest;
import com.ieji.rpg.domain.dto.mensagem.MensagemChatResponse;
import com.ieji.rpg.domain.entity.CasoInvestigacao;
import com.ieji.rpg.domain.entity.MensagemChat;
import com.ieji.rpg.domain.entity.Personagem;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.infra.repository.CasoInvestigacaoRepository;
import com.ieji.rpg.infra.repository.MensagemChatRepository;
import com.ieji.rpg.infra.repository.PersonagemRepository;
import com.ieji.rpg.infra.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
/// salvarMensagem(): monta e persiste uma nova mensagem de chat.
/// Busca o caso pelo id (lança exceção se não existir).
/// Busca o autor (usuário) pelo id (lança exceção se não existir).
/// Resolve o nome de exibição do autor via resolverNomeExibicao().
/// Constrói a entidade MensagemChat com caso, autor, conteúdo, data/hora atual
/// e nome de exibição, salva no repositório e retorna a resposta convertida.
///
/// resolverNomeExibicao(): define qual nome aparece no chat.
/// Se um personagemId foi informado, procura esse personagem garantindo que
/// pertence ao autor (via filter); se encontrado, retorna "nomeJogador - username".
/// Caso contrário (ou se o personagem não pertence ao autor), busca o último
/// personagem cadastrado pelo usuário (ordenado por id desc) e monta o mesmo
/// formato de nome; se o usuário não tiver nenhum personagem, retorna apenas o username.
///
/// create(): sobrescreve o create() genérico do AbstractService apenas para
/// aplicar a anotação @Transactional; delega toda a lógica para o super.create().
///
/// construct(): define quem é o autor da mensagem a partir do contexto de segurança.
/// Se houver um usuário autenticado (não anônimo) no SecurityContextHolder, usa o id
/// dele como autor. Caso contrário, tenta usar o authorId vindo do próprio DTO
/// (fluxo alternativo, ex.: chamada interna/sistema). Se nenhum dos dois estiver
/// disponível, lança exceção pois não é possível identificar o autor.
///
/// updateData(): atualiza apenas o conteúdo da mensagem, e somente se o novo
/// conteúdo enviado no DTO não for nulo.
///
/// convertToResponse(): converte a entidade MensagemChat para o DTO de resposta.
///
/// listarHistoricoDoCaso(): retorna todas as mensagens de um caso específico,
/// ordenadas por data de envio (ascendente), já convertidas para o DTO de resposta.
@Service
public class MensagemChatService extends AbstractService<MensagemChat, Integer, MensagemChatRequest, MensagemChatResponse> {

    private final CasoInvestigacaoRepository casoRepository;
    private final UserRepository usuarioRepository;

    private final PersonagemRepository personagemRepository;



    public MensagemChatService(MensagemChatRepository repository,
                               CasoInvestigacaoRepository casoRepository,
                               UserRepository usuarioRepository,
                               PersonagemRepository personagemRepository) {
        super(repository);
        this.casoRepository = casoRepository;
        this.usuarioRepository = usuarioRepository;
        this.personagemRepository = personagemRepository;
    }

    @Transactional
    public MensagemChatResponse salvarMensagem(Integer casoId, Integer autorId, Integer personagemId, String conteudo) {
        CasoInvestigacao caso = casoRepository.findById(casoId)
                .orElseThrow(() -> new EntityNotFoundException("Caso não encontrado"));
        Usuario autor = usuarioRepository.findById(autorId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + autorId));

        String nomeExibicao = resolverNomeExibicao(autor, personagemId);

        MensagemChat mensagem = MensagemChat.builder()
                .caso(caso)
                .autor(autor)
                .conteudo(conteudo)
                .enviadoEm(Instant.now())
                .nomeExibicao(nomeExibicao)
                .build();

        MensagemChat savedMsg = repository.save(mensagem);
        return convertToResponse(savedMsg);
    }

    private String resolverNomeExibicao(Usuario autor, Integer personagemId) {
        if (personagemId != null) {
            Optional<Personagem> personagemEscolhido = personagemRepository.findById(personagemId)
                    .filter(p -> p.getUsuario().getId().equals(autor.getId()));
            if (personagemEscolhido.isPresent()) {
                return personagemEscolhido.get().getNomeJogador() + " - " + autor.getUsername();
            }
        }
        return personagemRepository.findFirstByUsuarioIdOrderByIdPersonagemDesc(autor.getId())
                .map(p -> p.getNomeJogador() + " - " + autor.getUsername())
                .orElse(autor.getUsername());
    }
    @Override
    @Transactional
    public Optional<MensagemChatResponse> create(MensagemChatRequest dto) {
        return super.create(dto);
    }

    @Override
    protected MensagemChatResponse construct(MensagemChatRequest dto) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Usuario usuarioLogado = (Usuario) auth.getPrincipal();
            return salvarMensagem(dto.idCaso(), usuarioLogado.getId(), dto.personagemId(), dto.conteudo());
        }

        if (dto.authorId() != null) {
            return salvarMensagem(dto.idCaso(), dto.authorId(), dto.personagemId(), dto.conteudo());
        }

        throw new RuntimeException("Não foi possível identificar o autor da mensagem.");
    }

    @Override
    protected void updateData(MensagemChat entity, MensagemChatRequest dto) {
        if (dto.conteudo() != null) {
            entity.setConteudo(dto.conteudo());
        }
    }

    @Override
    protected MensagemChatResponse convertToResponse(MensagemChat entity) {
        return MensagemChatResponse.constructByEntity(entity);
    }

    public List<MensagemChatResponse> listarHistoricoDoCaso(Integer casoId) {
        return ((MensagemChatRepository) repository)
                .findByCasoIdCasoOrderByEnviadoEmAsc(casoId)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }
}