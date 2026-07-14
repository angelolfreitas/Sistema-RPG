package com.ieji.rpg.infra.security;
import com.ieji.rpg.infra.repository.PersonagemRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
@Component
@NullMarked
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private PersonagemRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // O JPA automaticamente trará a instância real (Tutor ou Veterinario) baseada na tabela de junção
        return userRepository.findByNome(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o e-mail: " + username));
    }
}