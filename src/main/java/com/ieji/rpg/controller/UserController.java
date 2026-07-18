package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.password.ForgotPasswordRequest;
import com.ieji.rpg.domain.dto.password.ResetPasswordRequest;
import com.ieji.rpg.domain.dto.user.AdminRegisterRequest;
import com.ieji.rpg.domain.dto.user.LoginRequest;
import com.ieji.rpg.domain.dto.user.LoginResponse;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.domain.entity.role.Role;
import com.ieji.rpg.service.AbstractService;
import com.ieji.rpg.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class UserController extends AbstractController<Usuario, Integer, LoginRequest, LoginResponse> {
    protected UserController(UserService service) {
        super(service);
    }

    @Override
    @PreAuthorize("hasAuthority('admin::write')")
    public ResponseEntity<LoginResponse> update(@RequestBody LoginRequest dto) { return super.update(dto); }

    @Override
    @PreAuthorize("hasAnyAuthority('manager::write', 'admin::write')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) { return super.delete(id); }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalState(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
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

    @PreAuthorize("hasAuthority('admin::write')")
    @PostMapping("/admin/register")
    public ResponseEntity<LoginResponse> adminRegister(@RequestBody AdminRegisterRequest request) {
        try {
            LoginResponse loginResponse = ((UserService) service).constructAdmin(request.usuario(), request.role());
            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PreAuthorize("hasAuthority('admin::write')")
    @PatchMapping("/{id}/role")
    public ResponseEntity<Void> alterarRole(@PathVariable Integer id, @RequestBody Map<String, Role> body) {
        Role novaRole = body.get("role");
        if (novaRole == null) return ResponseEntity.badRequest().build();
        ((UserService) service).alterarRole(id, novaRole);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        ((UserService) service).solicitarResetSenha(req.email());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest req) {
        try {
            ((UserService) service).resetarSenha(req.token(), req.novaSenha());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}
