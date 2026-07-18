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
        CasoInvestigacao caso = casoInvestigacaoRepository.findById(idCaso)
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
                .filter(s -> s.getCaso().getIdCaso().equals(idCaso))
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