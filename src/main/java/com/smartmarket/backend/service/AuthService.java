package com.smartmarket.backend.service;

import com.smartmarket.backend.model.User;
import com.smartmarket.backend.model.enums.UserRole;
import com.smartmarket.backend.repository.UserRepository;
import com.smartmarket.backend.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final AuditService auditService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager, AuditService auditService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.auditService = auditService;
    }

    public User register(String username, String email, String rawPassword) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.setRole(UserRole.USER);
        User saved = userRepository.save(user);
        auditService.record(user.getUsername(), "register", "user", "SUCCESS", "user_registered");
        return saved;
    }

    public String login(String username, String password) {
        try {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
            authenticationManager.authenticate(authToken);
        } catch (Exception e) {
            auditService.record(username, "login", "user", "FAILED", "bad_credentials");
            throw new BadCredentialsException("Credenciales inválidas");
        }

        User user = userRepository.findByUsername(username).orElseThrow();
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("uid", user.getId());
        String token = jwtUtil.generateToken(user.getUsername(), claims);
        auditService.record(username, "login", "user", "SUCCESS", "user_logged_in");
        return token;
    }

    public User getByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }
}
