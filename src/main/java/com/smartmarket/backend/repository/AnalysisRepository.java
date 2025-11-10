package com.smartmarket.backend.repository;

import com.smartmarket.backend.model.Analysis;
import com.smartmarket.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    List<Analysis> findByUserOrderByCreatedAtDesc(User user);
}