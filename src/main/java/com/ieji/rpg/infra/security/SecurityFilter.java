package com.ieji.rpg.infra.security;


import com.ieji.rpg.controller.MonstroController;
import com.ieji.rpg.domain.entity.Personagem;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.infra.repository.PersonagemRepository;
import com.ieji.rpg.infra.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
//estudar filter depois
@Component
public class SecurityFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(SecurityFilter.class);


    @Autowired
    TokenService tokenService;
    @Autowired
    UserRepository userRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        var login = tokenService.validateToken(token);

        if (login != null) {
            userRepository.findByEmail(login).ifPresent(user -> {
                var authorities = user.getAuthorities();
                log.info("SecurityFilter -> login={}, authorities={}", login, authorities);
                var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        } else {
            log.info("SecurityFilter -> token inválido ou ausente. Token bruto: {}", token);
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if(authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}