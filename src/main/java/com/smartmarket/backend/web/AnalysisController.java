package com.smartmarket.backend.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.smartmarket.backend.model.Analysis;
import com.smartmarket.backend.service.AnalysisService;
import com.smartmarket.backend.web.dto.AnalysisRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/analyses")
@Tag(name = "Analyses")
public class AnalysisController {
    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping
    @Operation(summary = "Crear análisis", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Analysis> analyze(Authentication auth, @Valid @RequestBody AnalysisRequest request) {
        Analysis a = analysisService.analyzeForUser(auth.getName(), request.getProductId(), request.getData());
        return ResponseEntity.ok(a);
    }

    @GetMapping("/me")
    @Operation(summary = "Listar análisis del usuario", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<Analysis>> myAnalyses(Authentication auth) {
        return ResponseEntity.ok(analysisService.listForUser(auth.getName()));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar análisis global", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<Analysis>> all() {
        return ResponseEntity.ok(analysisService.listAll());
    }
}
