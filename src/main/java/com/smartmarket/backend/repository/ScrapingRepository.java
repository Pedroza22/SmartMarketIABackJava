package com.smartmarket.backend.repository;

import com.smartmarket.backend.model.ScrapingResult;
import com.smartmarket.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScrapingRepository extends JpaRepository<ScrapingResult, Long> {
    List<ScrapingResult> findByUserOrderByCreatedAtDesc(User user);
}
