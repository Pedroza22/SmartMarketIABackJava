package com.smartmarket.backend.web;

import com.smartmarket.backend.repository.UserRepository;
import com.smartmarket.backend.repository.ProductRepository;
import com.smartmarket.backend.repository.SubscriptionRepository;
import com.smartmarket.backend.repository.AnalysisRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/db-status")
@PreAuthorize("hasRole('ADMIN')")
public class HealthController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final AnalysisRepository analysisRepository;

    public HealthController(UserRepository userRepository,
                            ProductRepository productRepository,
                            SubscriptionRepository subscriptionRepository,
                            AnalysisRepository analysisRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.analysisRepository = analysisRepository;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> m = new HashMap<>();
        m.put("db", "up");
        m.put("users", userRepository.count());
        m.put("products", productRepository.count());
        m.put("subscriptions", subscriptionRepository.count());
        m.put("analyses", analysisRepository.count());
        return ResponseEntity.ok(m);
    }
}
