package com.ieji.rpg.service;

import com.ieji.rpg.domain.dto.sessao.AgendarSessaoRequest;
import com.ieji.rpg.domain.dto.sessao.SessaoAgendadaResponse;
import com.ieji.rpg.domain.entity.CasoInvestigacao;
import com.ieji.rpg.domain.entity.SessaoAgendada;
import com.ieji.rpg.domain.entity.role.Role;
import com.ieji.rpg.infra.repository.CasoInvestigacaoRepository;
import com.ieji.rpg.infra.repository.SessaoAgendadaRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/// agendar(): cria uma nova sessão agendada para um caso.
/// Busca o caso pelo id (lança exceção se não existir), monta a entidade
/// SessaoAgendada com o conteúdo e a data informados, salva no repositório
/// e dispara a notificação por e-mail para os participantes via
/// notificarParticipantes(), retornando a resposta convertida.
///
/// listar(): retorna todas as sessões agendadas de um caso, ordenadas
/// pela data da sessão (ascendente), já convertidas para o DTO de resposta.
///
/// notificarParticipantes(): monta e envia o e-mail de aviso de nova sessão.
/// Formata a data da sessão no padrão pt-BR, monta o assunto com o nome do caso
/// e, para cada jogador do caso que não seja ADMIN, monta um corpo de e-mail
/// personalizado (com username, nome do caso, data/horário e o aviso do mestre)
/// e envia via EmailService.
///
/// toResponse(): converte a entidade SessaoAgendada para o DTO de resposta,
/// extraindo id do caso, conteúdo, data da sessão e data de criação.
///
/// cancelar(): remove uma sessão agendada.
/// Busca a sessão pelo id garantindo que ela pertence ao caso informado
/// (lança exceção se não encontrar), dispara a notificação de cancelamento
/// via notificarCancelamento() e então apaga a sessão do repositório.
///
/// notificarCancelamento(): monta e envia o e-mail de aviso de cancelamento.
/// Formata a data da sessão, monta o assunto e, para cada jogador do caso
/// com papel USER ou MANAGER, monta o corpo do e-mail informando que a
/// sessão foi desmarcada pelo mestre e envia via EmailService.
@Service
public class SessaoAgendadaService {

    private static final DateTimeFormatter FORMATADOR =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
                    .withLocale(new Locale("pt", "BR"));

    private final SessaoAgendadaRepository sessaoAgendadaRepository;
    private final CasoInvestigacaoRepository casoInvestigacaoRepository;

    @Autowired
    private EmailService emailService;

    public SessaoAgendadaService(SessaoAgendadaRepository sessaoAgendadaRepository,
                                 CasoInvestigacaoRepository casoInvestigacaoRepository) {
        this.sessaoAgendadaRepository = sessaoAgendadaRepository;
        this.casoInvestigacaoRepository = casoInvestigacaoRepository;
    }

    @Transactional
    public SessaoAgendadaResponse agendar(Integer idCaso, AgendarSessaoRequest request) {
        CasoInvestigacao caso = casoInvestigacaoRepository.findById(idCaso)//fazer excecao
                .orElseThrow(() -> new EntityNotFoundException("Caso não encontrado"));

        SessaoAgendada sessao = SessaoAgendada.builder()
                .caso(caso)
                .conteudo(request.conteudo())
                .dataSessao(request.dataSessao())
                .build();

        sessaoAgendadaRepository.save(sessao);
        notificarParticipantes(caso, sessao);

        return toResponse(sessao);
    }

    public List<SessaoAgendadaResponse> listar(Integer idCaso) {
        return sessaoAgendadaRepository.findByCaso_IdCasoOrderByDataSessaoAsc(idCaso).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void notificarParticipantes(CasoInvestigacao caso, SessaoAgendada sessao) {
        String dataFormatada = sessao.getDataSessao().format(FORMATADOR);
        String assunto = "Nova sessão marcada — " + caso.getNomeCaso();

        caso.getJogadores().stream()
                .filter(usuario -> usuario.getRole() != Role.ADMIN)
                .forEach(usuario -> {
                    String corpo = "Agente " + usuario.getUsername() + ",\n\n"
                            + "O mestre marcou uma nova sessão para o caso \"" + caso.getNomeCaso() + "\".\n\n"
                            + "Data e horário: " + dataFormatada + "\n\n"
                            + "Aviso do mestre:\n" + sessao.getConteudo();

                    emailService.enviar(usuario.getEmail(), assunto, corpo);
                });
    }

    private SessaoAgendadaResponse toResponse(SessaoAgendada sessao) {
        return new SessaoAgendadaResponse(
                sessao.getId(),
                sessao.getCaso().getIdCaso(),
                sessao.getConteudo(),
                sessao.getDataSessao(),
                sessao.getCriadoEm()
        );


    }

    @Transactional
    public void cancelar(Integer idCaso, Integer idSessao) {
        SessaoAgendada sessao = sessaoAgendadaRepository.findById(idSessao)
                .filter(s -> s.getCaso().getIdCaso().equals(idCaso))//fazer excecao
                .orElseThrow(() -> new EntityNotFoundException("Sessão agendada não encontrada"));

        notificarCancelamento(sessao.getCaso(), sessao);
        sessaoAgendadaRepository.delete(sessao);
    }

    private void notificarCancelamento(CasoInvestigacao caso, SessaoAgendada sessao) {
        String dataFormatada = sessao.getDataSessao().format(FORMATADOR);
        String assunto = "Sessão desmarcada — " + caso.getNomeCaso();

        caso.getJogadores().stream()
                .filter(usuario -> usuario.getRole() == Role.USER || usuario.getRole() == Role.MANAGER)
                .forEach(usuario -> {
                    String corpo = "Agente " + usuario.getUsername() + ",\n\n"
                            + "A sessão marcada para " + dataFormatada + " no caso \"" + caso.getNomeCaso() + "\" foi desmarcada pelo mestre.\n\n"
                            + "Aviso original:\n" + sessao.getConteudo();

                    emailService.enviar(usuario.getEmail(), assunto, corpo);
                });
    }
}