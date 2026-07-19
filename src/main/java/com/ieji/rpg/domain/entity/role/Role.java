package com.ieji.rpg.domain.entity.role;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
/// enum com os papéis (roles) de usuário do sistema: ADMIN, MANAGER, USER.
/// Cada papel possui um conjunto (roleAuthorities) de Authorities associadas,
/// sendo cumulativo em nível de acesso (ADMIN > MANAGER > USER).
///
/// getAuthorities(): converte o conjunto de Authorities do papel em uma
/// lista de SimpleGrantedAuthority (formato usado pelo Spring Security),
/// e adiciona também a authority "ROLE_" + nome do papel (ex.: "ROLE_ADMIN"),
/// necessária para checagens baseadas em hasRole().
@RequiredArgsConstructor
public enum Role {
    ADMIN
            (
                    Set.of(
                            Authorities.ADMIN_MANAGE,
                            Authorities.ADMIN_WRITE,
                            Authorities.ADMIN_DELETE,
                            Authorities.ADMIN_READ,
                            Authorities.MANAGER_MANAGE,
                            Authorities.MANAGER_WRITE,
                            Authorities.MANAGER_DELETE,
                            Authorities.MANAGER_READ,
                            Authorities.USER_WRITE,
                            Authorities.USER_DELETE,
                            Authorities.USER_READ
                    )
            ),
    MANAGER
            (
                    Set.of(
                            Authorities.MANAGER_MANAGE,
                            Authorities.MANAGER_WRITE,
                            Authorities.MANAGER_DELETE,
                            Authorities.MANAGER_READ,
                            Authorities.USER_WRITE,
                            Authorities.USER_DELETE,
                            Authorities.USER_READ
                    )
            ),
    USER
            (
                    Set.of(
                            Authorities.USER_READ,
                            Authorities.USER_WRITE,
                            Authorities.USER_DELETE
                    )
            )
    ;
    @Getter
    private final Set<Authorities> roleAuthorities;

    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = new java.util.ArrayList<>(getRoleAuthorities()
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}