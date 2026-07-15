package com.ieji.rpg.domain.dto.user;

import com.ieji.rpg.domain.dto.BaseDTO;

public record LoginRequest(String username, String login, String password) implements BaseDTO<Integer> {
    @Override
    public Integer getId() {
        return 0;
    }
}
