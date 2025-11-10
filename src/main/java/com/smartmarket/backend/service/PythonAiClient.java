package com.smartmarket.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class PythonAiClient {

    private static final Logger log = LoggerFactory.getLogger(PythonAiClient.class);

    private final RestTemplate restTemplate;

    @Value("${python.service.base-url}")
    private String baseUrl;

    public PythonAiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 500))
    public Map<String, Object> analyze(Map<String, Object> payload) {
        String url = baseUrl + "/api/analyze";
        long start = System.currentTimeMillis();
        log.info("python_request action=analyze url={} payload={}", url, payload);
        Map<String, Object> response = restTemplate.postForObject(url, payload, Map.class);
        long duration = System.currentTimeMillis() - start;
        log.info("python_response action=analyze duration_ms={} response={}", duration, response);
        return response;
    }

    @Recover
    public Map<String, Object> onAnalyzeFailure(Throwable t, Map<String, Object> payload) {
        log.error("python_error action=analyze error={} payload={}", t.toString(), payload);
        return Map.of("status", "error", "message", "Python service unavailable");
    }
}