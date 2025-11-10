package com.smartmarket.backend.service;

import com.smartmarket.backend.model.Subscription;
import com.smartmarket.backend.model.User;
import com.smartmarket.backend.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    public Subscription create(Subscription s) {
        return subscriptionRepository.save(s);
    }

    public List<Subscription> listByUser(User user) {
        return subscriptionRepository.findByUser(user);
    }
}