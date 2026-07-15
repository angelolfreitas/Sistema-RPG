package com.ieji.rpg.service;

import com.ieji.rpg.domain.dto.user.LoginRequest;
import com.ieji.rpg.domain.dto.user.LoginResponse;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.domain.entity.role.Role;
import com.ieji.rpg.infra.repository.UserRepository;
import com.ieji.rpg.infra.security.TokenService;
import jakarta.servlet.http.HttpServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService extends AbstractService <Usuario, Integer, LoginRequest, LoginResponse>{
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    public UserService(UserRepository repository) {
        super(repository);
    }

    public LoginResponse login(LoginRequest data) {
        Usuario user = ((UserRepository)repository).findByEmail(data.login())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (passwordEncoder.matches(data.password(), user.getPassword())) {
            String token = this.tokenService.generateToken(user);
            return new LoginResponse(user.getId(), user.getUsername(), token);
        }

        throw new RuntimeException("Senha inválida.");
    }


    @Override
    LoginResponse construct(LoginRequest object) {
        Usuario usuario = Usuario.builder()
                .role(Role.USER)
                .username(object.username())
                .password(passwordEncoder.encode(object.password()))
                .email(object.login())
                .build();

        repository.save(usuario);
        String token = this.tokenService.generateToken(usuario);
        return new LoginResponse(usuario.getId(), usuario.getUsername(), token);
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
        return new LoginResponse(entity.getId(), entity.getUsername(), token);
    }
}
