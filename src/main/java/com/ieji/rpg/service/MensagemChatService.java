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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MensagemChatService extends AbstractService<MensagemChat, Integer, MensagemChatRequest, MensagemChatResponse> {

    private final CasoInvestigacaoRepository casoRepository;
    private final UserRepository usuarioRepository;

    private final PersonagemRepository personagemRepository; // injetar no construtor



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
                .enviadoEm(LocalDateTime.now())
                .nomeExibicao(nomeExibicao)
                .build();

        MensagemChat savedMsg = repository.save(mensagem);
        return convertToResponse(savedMsg);
    }

    private String resolverNomeExibicao(Usuario autor, Integer personagemId) {
        if (personagemId != null) {
            // Só usa o personagem indicado se ele realmente pertencer ao autor da mensagem —
            // evita que alguém informe o id de outro jogador e "assine" com o personagem errado.
            Optional<Personagem> personagemEscolhido = personagemRepository.findById(personagemId)
                    .filter(p -> p.getUsuario().getId().equals(autor.getId()));
            if (personagemEscolhido.isPresent()) {
                return personagemEscolhido.get().getNomeJogador() + " - " + autor.getUsername();
            }
        }
        // Fallback: nenhum personagemId válido veio do front (ex: usuário sem personagem selecionado ainda)
        return personagemRepository.findFirstByUsuarioIdOrderByIdPersonagemDesc(autor.getId())
                .map(p -> p.getNomeJogador() + " - " + autor.getUsername())
                .orElse(autor.getUsername());
    }
    @Override
    @Transactional
    public Optional<MensagemChatResponse> create(MensagemChatRequest dto) {
        // Ao anotar o método público que inicia o fluxo REST, a transação é aberta pelo Proxy.
        // Qualquer self-invocation interna a partir daqui estará coberta pela transação principal.
        return super.create(dto);
    }

    @Override
    protected MensagemChatResponse construct(MensagemChatRequest dto) {
        // Apenas tenta buscar o usuário logado se for uma chamada REST (onde o SecurityContext existe)
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Usuario usuarioLogado = (Usuario) auth.getPrincipal();
            return salvarMensagem(dto.idCaso(), usuarioLogado.getId(), dto.personagemId(), dto.conteudo());
        }

        // Se não houver contexto (WebSocket), tentamos usar o autorId vindo do DTO
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