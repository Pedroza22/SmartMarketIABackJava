package com.smartmarket.backend.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "analyses")
public class Analysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @ManyToOne
    private Product product; // opcional

    @Column(length = 4000)
    private String inputDataJson;

    @Column(length = 4000)
    private String resultJson;

    @Column(length = 20)
    private String status; // PENDING, SUCCESS, FAILED

    private Long durationMs;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public String getInputDataJson() { return inputDataJson; }
    public void setInputDataJson(String inputDataJson) { this.inputDataJson = inputDataJson; }
    public String getResultJson() { return resultJson; }
    public void setResultJson(String resultJson) { this.resultJson = resultJson; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Long getDurationMs() { return durationMs; }
    public void setDurationMs(Long durationMs) { this.durationMs = durationMs; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}