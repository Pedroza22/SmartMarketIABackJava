package com.smartmarket.backend.web;

import com.smartmarket.backend.model.Subscription;
import com.smartmarket.backend.model.User;
import com.smartmarket.backend.repository.UserRepository;
import com.smartmarket.backend.service.SubscriptionService;
import com.smartmarket.backend.web.dto.SubscriptionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
@Tag(name = "Subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;

    public SubscriptionController(SubscriptionService subscriptionService, UserRepository userRepository) {
        this.subscriptionService = subscriptionService;
        this.userRepository = userRepository;
    }

    @PostMapping
    @Operation(summary = "Crear suscripción", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "400"),
        @ApiResponse(responseCode = "401"),
        @ApiResponse(responseCode = "500")
    })
    public ResponseEntity<Subscription> create(Authentication auth, @Valid @RequestBody SubscriptionRequest request) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        Subscription s = new Subscription();
        s.setUser(user);
        s.setPlan(request.getPlan());
        s.setStatus("ACTIVE");
        return ResponseEntity.ok(subscriptionService.create(s));
    }

    @GetMapping("/me")
    @Operation(summary = "Listar suscripciones del usuario", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "401"),
        @ApiResponse(responseCode = "500")
    })
    public ResponseEntity<List<Subscription>> listMy(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        return ResponseEntity.ok(subscriptionService.listByUser(user));
    }
}
