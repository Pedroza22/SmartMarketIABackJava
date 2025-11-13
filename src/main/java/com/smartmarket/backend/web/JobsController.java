package com.smartmarket.backend.web;

import com.smartmarket.backend.model.Job;
import com.smartmarket.backend.service.JobService;
import com.smartmarket.backend.web.dto.JobCreateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
@Tag(name = "Jobs")
public class JobsController {
    private final JobService jobService;

    public JobsController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    @Operation(summary = "Crear job", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Job> create(Authentication auth, @Valid @RequestBody JobCreateRequest request) {
        return ResponseEntity.ok(jobService.create(auth.getName(), request.getType(), request.getPayload()));
    }

    @PostMapping("/{id}/pause")
    @Operation(summary = "Pausar job", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Job> pause(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.pause(id));
    }

    @PostMapping("/{id}/resume")
    @Operation(summary = "Reanudar job", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Job> resume(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.resume(id));
    }

    @PostMapping("/{id}/retry")
    @Operation(summary = "Reintentar job", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Job> retry(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.retry(id));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancelar job", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Job> cancel(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.cancel(id));
    }

    @GetMapping
    @Operation(summary = "Listar jobs", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<Job>> list() {
        return ResponseEntity.ok(jobService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener job", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Job> get(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.get(id));
    }
}
