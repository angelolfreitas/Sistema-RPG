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
            boolean adminJaExiste = authentication.getByEmail(adminEmail);

            if (adminJaExiste) return;

            try {
                authentication.constructAdmin(
                        new LoginRequest("mestre", adminEmail, adminPassword),
                        Role.ADMIN);
            } catch (Exception e) {
            }


        };
    }
}