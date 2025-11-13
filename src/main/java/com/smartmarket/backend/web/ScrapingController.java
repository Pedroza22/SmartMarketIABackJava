package com.smartmarket.backend.web;

import com.smartmarket.backend.model.ScrapingResult;
import com.smartmarket.backend.service.ScrapingService;
import com.smartmarket.backend.web.dto.ScrapeGenericRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scraping")
public class ScrapingController {
    private final ScrapingService scrapingService;

    public ScrapingController(ScrapingService scrapingService) {
        this.scrapingService = scrapingService;
    }

    @PostMapping("/ml/{mlId}")
    public ResponseEntity<ScrapingResult> scrapeMl(Authentication auth, @PathVariable String mlId) {
        return ResponseEntity.ok(scrapingService.scrapeMl(auth.getName(), mlId));
    }

    @PostMapping("/generic")
    public ResponseEntity<ScrapingResult> scrapeGeneric(Authentication auth,
                                                       @Valid @RequestBody ScrapeGenericRequest request) {
        return ResponseEntity.ok(scrapingService.scrapeGeneric(auth.getName(), request.getUrl()));
    }

    @GetMapping("/me")
    public ResponseEntity<List<ScrapingResult>> myScrapes(Authentication auth) {
        return ResponseEntity.ok(scrapingService.listForUser(auth.getName()));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ScrapingResult>> all() {
        return ResponseEntity.ok(scrapingService.listAll());
    }
}
