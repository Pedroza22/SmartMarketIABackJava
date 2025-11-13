package com.smartmarket.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
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
    @CircuitBreaker(name = "pythonService", fallbackMethod = "onAnalyzeFailure")
    @RateLimiter(name = "pythonService")
    public Map<String, Object> analyze(Map<String, Object> payload) {
        String url = baseUrl + "/api/analyze";
        long start = System.currentTimeMillis();
        log.info("python_request action=analyze url={} payload={}", url, payload);
        Map<String, Object> response = restTemplate.postForObject(url, payload, Map.class);
        long duration = System.currentTimeMillis() - start;
        log.info("python_response action=analyze duration_ms={} response={}", duration, response);
        return response;
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 500))
    @CircuitBreaker(name = "pythonService", fallbackMethod = "onSyncFailure")
    @RateLimiter(name = "pythonService")
    public Map<String, Object> syncProductMl(String mlId) {
        String url = baseUrl + "/api/ml/sync/" + mlId;
        long start = System.currentTimeMillis();
        log.info("python_request action=ml_sync url={}", url);
        Map<String, Object> response = restTemplate.postForObject(url, Map.of(), Map.class);
        long duration = System.currentTimeMillis() - start;
        log.info("python_response action=ml_sync duration_ms={} response={}", duration, response);
        return response;
    }

    @Recover
    public Map<String, Object> onAnalyzeFailure(Throwable t, Map<String, Object> payload) {
        log.error("python_error action=analyze error={} payload={}", t.toString(), payload);
        return Map.of("status", "error", "message", "Python service unavailable");
    }

    public Map<String, Object> onSyncFailure(Throwable t, String mlId) {
        log.error("python_error action=ml_sync error={} mlId={}", t.toString(), mlId);
        return Map.of("status", "error", "message", "Python service unavailable", "mlId", mlId);
    }
}
