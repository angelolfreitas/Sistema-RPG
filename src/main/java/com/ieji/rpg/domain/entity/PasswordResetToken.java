package com.ieji.rpg.domain.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
/// tabela de tokens de redefinição de senha.
/// Possui o id do token
/// o token (string única gerada para o link de reset)
/// o usuario associado (relação N:1 com Usuario)
/// o expiraEm (instante de expiração, tipicamente 1 hora após a criação)
/// o usado (flag indicando se o token já foi consumido, default false)
@Entity
@Table(name = "password_reset_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "expira_em", nullable = false)
    private Instant expiraEm;

    @Column(nullable = false)
    @Builder.Default
    private boolean usado = false;
}