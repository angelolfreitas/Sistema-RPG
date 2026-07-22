package com.ieji.rpg.controller;

import com.ieji.rpg.domain.dto.password.ForgotPasswordRequest;
import com.ieji.rpg.domain.dto.password.ResetPasswordRequest;
import com.ieji.rpg.domain.dto.user.AdminRegisterRequest;
import com.ieji.rpg.domain.dto.user.LoginRequest;
import com.ieji.rpg.domain.dto.user.LoginResponse;
import com.ieji.rpg.domain.entity.Usuario;
import com.ieji.rpg.domain.entity.role.Role;
import com.ieji.rpg.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
/// Aprovveita as superdefinicopes e só restringe o acesso
///
/// O método hanldeIllegalstate trata excessoes para o logger.
///
/// login(): Recebe um login response e tenta logar o usuario. Pode retornar uma excessao.
///
/// register(): Tenta registrar e pode retornar uma excessao 9em maior parte por emails iguais).
///
/// adminRegister(): registro de usuaurios com o campo de role
///
/// alterarRole(): permite alterar a role dos usuarios. Util no chat para o mestre.
///
/// forgotPassword() metodefinicao de senha que utiliza o service de email.
///
/// rresetpassword():: tela de nova senha, após passsar pelo forgotPassword().
@RestController
@RequestMapping("/auth")
public class UserController extends AbstractController<Usuario, Integer, LoginRequest, LoginResponse> {
    private final UserService userService;
    protected UserController(UserService service) {
        super(service);
        this.userService = service;
    }

    @Override
    @PreAuthorize("hasAuthority('admin::write')")
    public ResponseEntity<LoginResponse> update(@RequestBody LoginRequest dto) { return super.update(dto); }

    @Override
    @PreAuthorize("hasAnyAuthority('manager::write', 'admin::write')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) { return super.delete(id); }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest body) {
        return ResponseEntity.ok(userService.login(body));
    }

    @PreAuthorize("hasAuthority('admin::write')")
    @PostMapping("/admin/register")
    public ResponseEntity<LoginResponse> adminRegister(@RequestBody AdminRegisterRequest request) {
        return ResponseEntity.ok(userService.constructAdmin(request.usuario(), request.role()));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest req) {
        userService.resetarSenha(req.token(), req.novaSenha());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody LoginRequest body) {
        return service.create(body).map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }

    @PreAuthorize("hasAuthority('admin::write')")
    @PatchMapping("/{id}/role")
    public ResponseEntity<Void> alterarRole(@PathVariable Integer id, @RequestBody Map<String, Role> body) {
        Role novaRole = body.get("role");
        if (novaRole == null) return ResponseEntity.badRequest().build();
        userService.alterarRole(id, novaRole);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        userService.solicitarResetSenha(req.email());
        return ResponseEntity.ok().build();
    }
}
