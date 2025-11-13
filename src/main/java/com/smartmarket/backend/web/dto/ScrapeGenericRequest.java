package com.smartmarket.backend.web.dto;

import jakarta.validation.constraints.NotBlank;

public class ScrapeGenericRequest {
    @NotBlank
    private String url;

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}
