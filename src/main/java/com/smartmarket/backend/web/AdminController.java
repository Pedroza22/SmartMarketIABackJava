package com.smartmarket.backend.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartmarket.backend.repository.AnalysisRepository;
import com.smartmarket.backend.repository.ProductRepository;
import com.smartmarket.backend.repository.SubscriptionRepository;
import com.smartmarket.backend.repository.UserRepository;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final AnalysisRepository analysisRepository;

    public AdminController(UserRepository userRepository, ProductRepository productRepository,
                           SubscriptionRepository subscriptionRepository, AnalysisRepository analysisRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.analysisRepository = analysisRepository;
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> metrics() {
        Map<String, Object> m = new HashMap<>();
        m.put("users", userRepository.count());
        m.put("products", productRepository.count());
        m.put("subscriptions", subscriptionRepository.count());
        m.put("analyses", analysisRepository.count());
        return ResponseEntity.ok(m);
    }
}
