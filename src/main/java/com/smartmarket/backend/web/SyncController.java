package com.smartmarket.backend.web;

import com.smartmarket.backend.repository.ProductRepository;
import com.smartmarket.backend.service.PythonAiClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/sync")
@Tag(name = "Sync")
public class SyncController {

    private final PythonAiClient pythonAiClient;
    private final ProductRepository productRepository;

    public SyncController(PythonAiClient pythonAiClient, ProductRepository productRepository) {
        this.pythonAiClient = pythonAiClient;
        this.productRepository = productRepository;
    }

    @PostMapping("/product/{ml_id}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @Operation(summary = "Sincronizar producto ML", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "400"),
        @ApiResponse(responseCode = "401"),
        @ApiResponse(responseCode = "403"),
        @ApiResponse(responseCode = "500")
    })
    public ResponseEntity<Map<String, Object>> syncProduct(@PathVariable("ml_id") String mlId) {
        productRepository.findByMlId(mlId).orElseThrow(() -> new IllegalArgumentException("Producto no registrado con mlId"));
        Map<String, Object> result = pythonAiClient.syncProductMl(mlId);
        return ResponseEntity.ok(result);
    }
}
