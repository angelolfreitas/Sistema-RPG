package com.ieji.rpg.infra.security;


import com.ieji.rpg.domain.dto.user.LoginRequest;
import com.ieji.rpg.domain.entity.role.Role;
import com.ieji.rpg.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminComponent {
    @Value("${spring.security.user.name}")
    private String adminEmail;
    @Value("${spring.security.user.password}")
    private String adminPassword;

    @Bean
    public CommandLineRunner initAdmin(UserService authentication) {
        return args -> {
            try {
                authentication.constructAdmin(
                        new LoginRequest("mestre", adminEmail, adminPassword),
                        Role.ADMIN
                );
            } catch (Exception e) {
                // Admin já existe de uma subida anterior — não é erro, é esperado
                System.out.println("Admin já existente, seed ignorado: " + e.getMessage());
            }
        };
    }
}