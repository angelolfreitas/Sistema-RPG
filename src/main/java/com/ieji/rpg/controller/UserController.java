package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.user.LoginRequest;
import com.ieji.rpg.domain.dto.user.LoginResponse;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.domain.entity.role.Role;
import com.ieji.rpg.service.AbstractService;
import com.ieji.rpg.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserController extends AbstractController<Usuario, Integer, LoginRequest, LoginResponse> {
    protected UserController(UserService service) {
        super(service);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest body) {
        try {
            LoginResponse response = ((UserService)service).login(body);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest body) {
        return service.create(body).map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }
}
