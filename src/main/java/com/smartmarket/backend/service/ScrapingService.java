package com.smartmarket.backend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartmarket.backend.model.Product;
import com.smartmarket.backend.model.ScrapingResult;
import com.smartmarket.backend.model.User;
import com.smartmarket.backend.repository.ProductRepository;
import com.smartmarket.backend.repository.ScrapingRepository;
import com.smartmarket.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ScrapingService {
    private final ScrapingRepository scrapingRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PythonAiClient pythonAiClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ScrapingService(ScrapingRepository scrapingRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           PythonAiClient pythonAiClient) {
        this.scrapingRepository = scrapingRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.pythonAiClient = pythonAiClient;
    }

    public ScrapingResult scrapeMl(String username, String mlId) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Map<String, Object> result = pythonAiClient.scrapeMl(mlId);
        Product product = productRepository.findByMlId(mlId).orElse(null);

        ScrapingResult sr = new ScrapingResult();
        sr.setUser(user);
        sr.setProduct(product);
        sr.setPlatform("ML");
        sr.setSourceUrl(null);
        try {
            sr.setResultJson(objectMapper.writeValueAsString(result));
        } catch (JsonProcessingException e) {
            sr.setResultJson("{\"error\":\"serialization\"}");
        }
        ScrapingResult saved = scrapingRepository.save(sr);
        return saved;
    }

    public ScrapingResult scrapeGeneric(String username, String url) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Map<String, Object> result = pythonAiClient.scrapeGeneric(url);

        ScrapingResult sr = new ScrapingResult();
        sr.setUser(user);
        sr.setProduct(null);
        sr.setPlatform("GENERIC");
        sr.setSourceUrl(url);
        try {
            sr.setResultJson(objectMapper.writeValueAsString(result));
        } catch (JsonProcessingException e) {
            sr.setResultJson("{\"error\":\"serialization\"}");
        }
        ScrapingResult saved = scrapingRepository.save(sr);
        return saved;
    }

    public List<ScrapingResult> listForUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        return scrapingRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<ScrapingResult> listAll() {
        return scrapingRepository.findAll();
    }
}
