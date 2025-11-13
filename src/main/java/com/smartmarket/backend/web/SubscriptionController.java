package com.smartmarket.backend.web;

import com.smartmarket.backend.model.Subscription;
import com.smartmarket.backend.model.User;
import com.smartmarket.backend.repository.UserRepository;
import com.smartmarket.backend.service.SubscriptionService;
import com.smartmarket.backend.web.dto.SubscriptionRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final UserRepository userRepository;

    public SubscriptionController(SubscriptionService subscriptionService, UserRepository userRepository) {
        this.subscriptionService = subscriptionService;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Subscription> create(Authentication auth, @Valid @RequestBody SubscriptionRequest request) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        Subscription s = new Subscription();
        s.setUser(user);
        s.setPlan(request.getPlan());
        s.setStatus("ACTIVE");
        return ResponseEntity.ok(subscriptionService.create(s));
    }

    @GetMapping("/me")
    public ResponseEntity<List<Subscription>> listMy(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        return ResponseEntity.ok(subscriptionService.listByUser(user));
    }
}
