package com.subscriptionservice.service;

import com.subscriptionservice.dto.SubscriptionDto;
import com.subscriptionservice.exception.ResourceNotFoundException;
import com.subscriptionservice.mapper.SubscriptionMapper;
import com.subscriptionservice.model.Subscription;
import com.subscriptionservice.model.User;
import com.subscriptionservice.repository.SubscriptionRepository;
import com.subscriptionservice.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SubscriptionMapper subscriptionMapper;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private User testUser;
    private Subscription testSubscription;
    private SubscriptionDto testSubscriptionDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("testuser");
        testUser.setLastName("testuser");
        testUser.setMiddleName("testuser");
        testUser.setEmail("test@example.com");

        testSubscription = new Subscription();
        testSubscription.setId(1L);
        testSubscription.setServiceName("Test Service");
        testSubscription.setPrice(9.99);
        testSubscription.setStartDate(OffsetDateTime.now());
        testSubscription.setEndDate(OffsetDateTime.now().plusMonths(1));
        testSubscription.setUser(testUser);

        testSubscriptionDto = new SubscriptionDto();
        testSubscriptionDto.setServiceName("Test Service");
        testSubscriptionDto.setPrice(new BigDecimal("9.99"));
        testSubscriptionDto.setStartDate(OffsetDateTime.now());
        testSubscriptionDto.setEndDate(OffsetDateTime.now().plusMonths(1));
        testSubscriptionDto.setUserId(testUser.getId());
    }

    @Test
    void createSubscription_ShouldReturnSubscriptionId() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(subscriptionMapper.toEntity(testSubscriptionDto, testUser)).thenReturn(testSubscription);
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);

        Long subscriptionId = subscriptionService.createSubscription(testSubscriptionDto);

        assertNotNull(subscriptionId);
        assertEquals(testSubscription.getId(), subscriptionId);
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    void createSubscription_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> 
            subscriptionService.createSubscription(testSubscriptionDto)
        );
    }

    @Test
    void getSubscriptionById_ShouldReturnSubscription() {
        when(subscriptionRepository.findById(testSubscription.getId()))
                .thenReturn(Optional.of(testSubscription));
        when(subscriptionMapper.toDto(testSubscription)).thenReturn(testSubscriptionDto);

        SubscriptionDto result = subscriptionService.getSubscriptionById(testSubscription.getId());

        assertNotNull(result);
        assertEquals(testSubscriptionDto.getServiceName(), result.getServiceName());
        assertEquals(testSubscriptionDto.getPrice(), result.getPrice());
    }

    @Test
    void getSubscriptionById_WhenNotFound_ShouldThrowException() {
        when(subscriptionRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            subscriptionService.getSubscriptionById(1L)
        );
    }

    @Test
    void updateSubscription_ShouldReturnUpdatedSubscription() {
        when(subscriptionRepository.findById(testSubscription.getId()))
                .thenReturn(Optional.of(testSubscription));
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(subscriptionRepository.save(any(Subscription.class))).thenReturn(testSubscription);
        when(subscriptionMapper.toDto(testSubscription)).thenReturn(testSubscriptionDto);

        SubscriptionDto result = subscriptionService.updateSubscription(
                testSubscription.getId(), testSubscriptionDto);

        assertNotNull(result);
        verify(subscriptionMapper).updateEntityFromDto(testSubscriptionDto, testSubscription, testUser);
    }

    @Test
    void deleteSubscription_ShouldDeleteSubscription() {
        when(subscriptionRepository.existsById(testSubscription.getId())).thenReturn(true);

        subscriptionService.deleteSubscription(testSubscription.getId());

        verify(subscriptionRepository).deleteById(testSubscription.getId());
    }

    @Test
    void getSubscriptionsByUserId_ShouldReturnUserSubscriptions() {
        when(userRepository.existsById(testUser.getId())).thenReturn(true);
        when(subscriptionRepository.findByUserId(testUser.getId()))
                .thenReturn(Arrays.asList(testSubscription));
        when(subscriptionMapper.toDto(testSubscription)).thenReturn(testSubscriptionDto);

        List<SubscriptionDto> result = subscriptionService.getSubscriptionsByUserId(testUser.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testSubscriptionDto.getServiceName(), result.get(0).getServiceName());
    }

    @Test
    void getTopSubscriptions_ShouldReturnTopSubscriptions() {
        List<Object[]> topSubscriptions = Arrays.asList(
                new Object[]{"Service1", 5L},
                new Object[]{"Service2", 3L}
        );
        when(subscriptionRepository.findTopSubscriptions()).thenReturn(topSubscriptions);

        List<Object[]> result = subscriptionService.getTopSubscriptions();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Service1", result.get(0)[0]);
        assertEquals(5L, result.get(0)[1]);
    }
} 