package com.smartmarket.backend.web;

import com.smartmarket.backend.model.Analysis;
import com.smartmarket.backend.service.AnalysisService;
import com.smartmarket.backend.web.dto.AnalysisRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analyses")
public class AnalysisController {
    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping
    public ResponseEntity<Analysis> analyze(Authentication auth, @Valid @RequestBody AnalysisRequest request) {
        Analysis a = analysisService.analyzeForUser(auth.getName(), request.getProductId(), request.getData());
        return ResponseEntity.ok(a);
    }

    @GetMapping("/me")
    public ResponseEntity<List<Analysis>> myAnalyses(Authentication auth) {
        return ResponseEntity.ok(analysisService.listForUser(auth.getName()));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Analysis>> all() {
        return ResponseEntity.ok(analysisService.listAll());
    }
}