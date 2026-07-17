package com.ieji.rpg.domain.dto.user;

import com.ieji.rpg.domain.entity.role.Role;

public record LoginResponse(Integer id, String username, String token, Role role) {
}
