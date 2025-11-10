package com.smartmarket.backend.repository;

import com.smartmarket.backend.model.Subscription;
import com.smartmarket.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUser(User user);
}