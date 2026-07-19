package com.ieji.rpg.service;

import com.ieji.rpg.domain.dto.user.LoginRequest;
import com.ieji.rpg.domain.dto.user.LoginResponse;
import com.ieji.rpg.domain.entity.*;
import com.ieji.rpg.domain.entity.role.Role;
import com.ieji.rpg.infra.repository.*;
import com.ieji.rpg.infra.security.TokenService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServlet;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService extends AbstractService <Usuario, Integer, LoginRequest, LoginResponse>{
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    private final PersonagemRepository personagemRepository;

    private final CasoInvestigacaoRepository casoInvestigacaoRepository;

    public UserService(UserRepository repository,
                       PersonagemRepository personagemRepository,
                       CasoInvestigacaoRepository casoInvestigacaoRepository,
                       MensagemChatRepository mensagemChat) {
        super(repository);
        this.personagemRepository = personagemRepository;
        this.casoInvestigacaoRepository = casoInvestigacaoRepository;
        this.mensagemChat = mensagemChat;
    }

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Autowired
    private PersonagemService personagemService;


    private final MensagemChatRepository mensagemChat;

    @Transactional
    public void solicitarResetSenha(String email) {
        ((UserRepository) repository).findByEmail(email).ifPresent(usuario -> {
            String token = UUID.randomUUID().toString();
            tokenRepository.save(PasswordResetToken.builder()
                    .token(token)
                    .usuario(usuario)
                    .expiraEm(Instant.now().plus(1, ChronoUnit.HOURS))
                    .usado(false)
                    .build());

            String link = frontendUrl + "/resetar-senha?token=" + token;
            emailService.enviar(
                    usuario.getEmail(),
                    "Redefinição de senha — Instituto Eleonora",
                    "Recebemos um pedido de redefinição de senha por parte de você, agente " +usuario.getUsername()+".\n"
                            +"É inadmissível que você esqueça suas credenciais, pois coloca os estudantes e o instituto em risco. Por favor, tenha um mínimo de cuidado da próxima vez.\n"
                            + link
            );
        });
    }

    @Transactional
    public void resetarSenha(String token, String novaSenha) {
        PasswordResetToken prt = tokenRepository.findByToken(token)
                .filter(t -> !t.isUsado() && t.getExpiraEm().isAfter(Instant.now()))
                .orElseThrow(() -> new IllegalArgumentException("Token inválido ou expirado."));

        Usuario usuario = prt.getUsuario();
        usuario.setPassword(passwordEncoder.encode(novaSenha));
        repository.save(usuario);

        prt.setUsado(true);
        tokenRepository.save(prt);
    }
    public LoginResponse login(LoginRequest data) {
        Usuario user = ((UserRepository)repository).findByEmail(data.login())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        if (passwordEncoder.matches(data.password(), user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            return new LoginResponse(user.getId(), user.getUsername(), token, user.getRole());
        }

        throw new RuntimeException("Senha inválida.");
    }


    @Override
    protected LoginResponse construct(LoginRequest object) {
        Usuario usuario = Usuario.builder()
                .role(Role.USER)
                .username(object.username())
                .password(passwordEncoder.encode(object.password()))
                .email(object.login())
                .build();

        repository.save(usuario);
        String token = this.tokenService.generateToken(usuario);
        return new LoginResponse(usuario.getId(), usuario.getUsername(), token, usuario.getRole());
    }

    public LoginResponse constructAdmin(LoginRequest object, Role role) {

        Optional<Usuario> findObject = repository.findById(object.getId());
        if(findObject.isPresent()){
            throw new RuntimeException("já Admin criado");
        }
        Usuario usuario = Usuario.builder()
                .role(role)
                .username(object.username())
                .password(passwordEncoder.encode(object.password()))
                .email(object.login())
                .build();

        repository.save(usuario);
        String token = this.tokenService.generateToken(usuario);
        return new LoginResponse(usuario.getId(), usuario.getUsername(), token, usuario.getRole());
    }

    @Override
    protected void updateData(Usuario entity, LoginRequest object) {
        entity.setPassword(passwordEncoder.encode(object.password()));
        entity.setEmail(object.login());
        entity.setUsername(object.username());
    }

    @Override
    protected LoginResponse convertToResponse(Usuario entity) {
        String token = this.tokenService.generateToken(entity);
        return new LoginResponse(entity.getId(), entity.getUsername(), token, entity.getRole());
    }

    @Transactional
    public void alterarRole(Integer id, Role novaRole) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        usuario.setRole(novaRole);
        repository.save(usuario);
    }


    @Override
    @Transactional
    public void delete(Integer id) {
        Usuario usuario = repository.findById(id).orElse(null);
        if (usuario == null) return;

        tokenRepository.deleteByUsuario_Id(id);

        List<CasoInvestigacao> todosCasos = casoInvestigacaoRepository.findAll();
        for (CasoInvestigacao caso : todosCasos) {
            if (caso.getJogadores().contains(usuario)) {
                caso.getJogadores().remove(usuario);
                casoInvestigacaoRepository.save(caso);
            }
        }

        // casos onde o usuário é mestre: transfere para outro admin em vez de apagar
        List<CasoInvestigacao> casosComoMestre = casoInvestigacaoRepository.findByMestre_Id(id);
        for (CasoInvestigacao caso : casosComoMestre) {
            Usuario substituto = encontrarSubstitutoMestre(caso, id);

            if (substituto == null) {
                throw new IllegalStateException(
                        "Não é possível excluir este usuário: ele é mestre da sessão \"" + caso.getNomeCaso() +
                                "\" e não há outro administrador cadastrado para assumi-la. " +
                                "Cadastre outro mestre (admin) antes de excluir esta conta."
                );
            }

            caso.setMestre(substituto);
            caso.getJogadores().remove(usuario); // se o mestre também estava como jogador, tira
            casoInvestigacaoRepository.save(caso);
        }

        List<Personagem> personagens = personagemRepository.findByUsuarioId(id);
        for (Personagem p : personagens) {
            personagemService.delete(p.getIdPersonagem());
        }

        // mensagens do usuário: mantém histórico, só desvincula
        List<MensagemChat> mensagens = mensagemChat.findByAutor_Id(id);
        mensagens.forEach(m -> m.setAutor(null));
        mensagemChat.saveAll(mensagens);

        repository.delete(usuario);
    }

    private Usuario encontrarSubstitutoMestre(CasoInvestigacao caso, Integer idExcluido) {
        // 1ª prioridade: outro ADMIN que já está na sessão como jogador
        Usuario candidatoNaSessao = caso.getJogadores().stream()
                .filter(u -> !u.getId().equals(idExcluido) && u.getRole() == Role.ADMIN)
                .findFirst()
                .orElse(null);

        if (candidatoNaSessao != null) return candidatoNaSessao;

        // 2ª prioridade: qualquer outro ADMIN do sistema
        return ((UserRepository) repository).findFirstByRoleAndIdNot(Role.ADMIN, idExcluido).orElse(null);
    }

    public boolean getByEmail(String adminEmail) {
        Optional<Usuario> user = ((UserRepository)repository).findByEmail(adminEmail);
        return user.isPresent();
    }
}