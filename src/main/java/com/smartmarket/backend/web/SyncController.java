package com.smartmarket.backend.web;

import com.smartmarket.backend.repository.ProductRepository;
import com.smartmarket.backend.service.PythonAiClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/sync")
public class SyncController {

    private final PythonAiClient pythonAiClient;
    private final ProductRepository productRepository;

    public SyncController(PythonAiClient pythonAiClient, ProductRepository productRepository) {
        this.pythonAiClient = pythonAiClient;
        this.productRepository = productRepository;
    }

    @PostMapping("/product/{ml_id}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    public ResponseEntity<Map<String, Object>> syncProduct(@PathVariable("ml_id") String mlId) {
        productRepository.findByMlId(mlId).orElseThrow(() -> new IllegalArgumentException("Producto no registrado con mlId"));
        Map<String, Object> result = pythonAiClient.syncProductMl(mlId);
        return ResponseEntity.ok(result);
    }
}
