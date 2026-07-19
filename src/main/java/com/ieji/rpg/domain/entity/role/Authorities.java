package com.ieji.rpg.domain.entity.role;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
/// enum com as autoridades (permissões granulares) do sistema.
/// Cada valor possui um name (String) usado como authority do Spring Security.
/// Dividido por nível de papel (admin, manager, user) e por tipo de ação
/// (read, write, delete, manage).
@RequiredArgsConstructor
public enum Authorities {
    ADMIN_READ("admin::read"),
    ADMIN_WRITE("admin::write"),
    ADMIN_DELETE("admin::delete"),
    ADMIN_MANAGE("admin::manage"),
    MANAGER_READ("manager::read"),
    MANAGER_WRITE("manager::write"),
    MANAGER_DELETE("manager::delete"),
    MANAGER_MANAGE("manager::manage"),
    USER_READ("user::read"),
    USER_WRITE("user::write"),
    USER_DELETE("user::delete");
    @Getter
    private final String name;
}
