package com.ieji.rpg.domain.entity;

import com.ieji.rpg.domain.entity.role.Role;
import jakarta.persistence.*;
import lombok.*;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
/// tabela de usuários.
/// Possui o id dos usuários
/// O nome
/// a senha
/// o email
/// a role (função)
/// possui o meptodo getAuthorities(), que retorna as roles do usuário)
/// sobrepoe a classe userDetails para os campos de senha, autoridade e nome, que serão
/// usados no tokenService.
///
/// Classe base para os demais relacionamentos
@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nome")
    private String username;

    @Column(name = "senha")
    private String password;

    @Column(name = "email")
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;
    @Override
    public @NullMarked Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }


    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public @NullMarked String getUsername() {
        return username;
    }
}
