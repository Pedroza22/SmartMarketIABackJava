package com.smartmarket.backend.web.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Map;

public class AnalysisRequest {
    private Long productId;
    @NotNull
    private Map<String, Object> data;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public Map<String, Object> getData() { return data; }
    public void setData(Map<String, Object> data) { this.data = data; }
}