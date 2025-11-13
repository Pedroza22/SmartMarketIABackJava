package com.smartmarket.backend.service;

import com.smartmarket.backend.model.AuditLog;
import com.smartmarket.backend.model.User;
import com.smartmarket.backend.repository.AuditLogRepository;
import com.smartmarket.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuditService {
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    public AuditService(AuditLogRepository auditLogRepository, UserRepository userRepository) {
        this.auditLogRepository = auditLogRepository;
        this.userRepository = userRepository;
    }

    public void record(String username, String action, String resource, String status, String message) {
        User user = null;
        if (username != null) {
            user = userRepository.findByUsername(username).orElse(null);
        }
        AuditLog log = new AuditLog();
        log.setUser(user);
        log.setAction(action);
        log.setResource(resource);
        log.setStatus(status);
        log.setMessage(message);
        log.setTraceId(UUID.randomUUID().toString());
        auditLogRepository.save(log);
    }
}
