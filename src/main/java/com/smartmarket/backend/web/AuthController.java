package com.smartmarket.backend.web;

import com.smartmarket.backend.model.User;
import com.smartmarket.backend.service.AuthService;
import com.smartmarket.backend.web.dto.AuthResponse;
import com.smartmarket.backend.web.dto.LoginRequest;
import com.smartmarket.backend.web.dto.RegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuario")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        User user = authService.register(request.getUsername(), request.getEmail(), request.getPassword());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    @Operation(summary = "Login de usuario")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request.getUsername(), request.getPassword());
        User user = authService.getByUsername(request.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getRole().name()));
    }
}
