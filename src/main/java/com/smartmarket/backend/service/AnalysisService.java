package com.smartmarket.backend.service;

import com.smartmarket.backend.model.Analysis;
import com.smartmarket.backend.model.Product;
import com.smartmarket.backend.model.User;
import com.smartmarket.backend.repository.AnalysisRepository;
import com.smartmarket.backend.repository.ProductRepository;
import com.smartmarket.backend.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AnalysisService {
    private static final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    private final AnalysisRepository analysisRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PythonAiClient pythonAiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AnalysisService(AnalysisRepository analysisRepository, ProductRepository productRepository,
                           UserRepository userRepository, PythonAiClient pythonAiClient) {
        this.analysisRepository = analysisRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.pythonAiClient = pythonAiClient;
    }

    public Analysis analyzeForUser(String username, Long productId, Map<String, Object> data) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Product product = null;
        if (productId != null) {
            product = productRepository.findById(productId).orElse(null);
        }

        Analysis analysis = new Analysis();
        analysis.setUser(user);
        analysis.setProduct(product);
        try {
            analysis.setInputDataJson(objectMapper.writeValueAsString(data));
        } catch (JsonProcessingException e) {
            analysis.setInputDataJson("{}");
        }
        analysis.setStatus("PENDING");
        analysis = analysisRepository.save(analysis);

        long start = System.currentTimeMillis();
        Map<String, Object> result = pythonAiClient.analyze(data);
        long duration = System.currentTimeMillis() - start;

        try {
            analysis.setResultJson(objectMapper.writeValueAsString(result));
        } catch (JsonProcessingException e) {
            analysis.setResultJson("{\"error\":\"serialization\"}");
        }
        analysis.setDurationMs(duration);
        analysis.setStatus("SUCCESS");
        if (result.containsKey("status") && "error".equals(result.get("status"))) {
            analysis.setStatus("FAILED");
        }
        analysis = analysisRepository.save(analysis);
        log.info("analysis_saved id={} status={} duration_ms={}", analysis.getId(), analysis.getStatus(), duration);
        return analysis;
    }

    public List<Analysis> listForUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return analysisRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Analysis> listAll() {
        return analysisRepository.findAll();
    }
}