package com.smartmarket.backend.web.dto;

import jakarta.validation.constraints.NotBlank;

public class SubscriptionRequest {
    @NotBlank
    private String plan;

    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
}