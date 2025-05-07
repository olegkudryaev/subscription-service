package com.subscriptionservice.service;

import com.subscriptionservice.dto.SubscriptionDto;
import com.subscriptionservice.exception.ResourceNotFoundException;
import com.subscriptionservice.mapper.SubscriptionMapper;
import com.subscriptionservice.model.Subscription;
import com.subscriptionservice.model.User;
import com.subscriptionservice.repository.SubscriptionRepository;
import com.subscriptionservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final SubscriptionMapper subscriptionMapper;

    @Transactional(readOnly = true)
    public List<SubscriptionDto> getAllSubscriptions() {
        return subscriptionRepository.findAll().stream()
                .map(subscriptionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public SubscriptionDto getSubscriptionById(Long id) {
        return subscriptionRepository.findById(id)
                .map(subscriptionMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));
    }

    @Transactional
    public Long createSubscription(SubscriptionDto subscriptionDto) {
        User user = userRepository.findById(subscriptionDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + subscriptionDto.getUserId()));
        
        Subscription subscription = subscriptionMapper.toEntity(subscriptionDto, user);
        return subscriptionRepository.save(subscription).getId();
    }

    @Transactional
    public SubscriptionDto updateSubscription(Long id, SubscriptionDto subscriptionDto) {
        Subscription existingSubscription = subscriptionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with id: " + id));
        
        User user = userRepository.findById(subscriptionDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + subscriptionDto.getUserId()));
        
        subscriptionMapper.updateEntityFromDto(subscriptionDto, existingSubscription, user);
        Subscription savedSubscription = subscriptionRepository.save(existingSubscription);
        return subscriptionMapper.toDto(savedSubscription);
    }

    @Transactional
    public void deleteSubscription(Long id) {
        if (!subscriptionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Subscription not found with id: " + id);
        }
        subscriptionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<SubscriptionDto> getSubscriptionsByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return subscriptionRepository.findByUserId(userId).stream()
                .map(subscriptionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Object[]> getTopSubscriptions() {
        return subscriptionRepository.findTopSubscriptions();
    }
} 