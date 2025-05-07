package com.subscriptionservice.repository;

import com.subscriptionservice.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUserId(Long userId);

    @Query("SELECT s.serviceName, COUNT(s) as count FROM Subscription s GROUP BY s.serviceName ORDER BY count DESC")
    List<Object[]> findTopSubscriptions();
} 