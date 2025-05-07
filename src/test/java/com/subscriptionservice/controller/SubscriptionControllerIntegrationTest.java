package com.subscriptionservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.subscriptionservice.dto.SubscriptionDto;
import com.subscriptionservice.model.Subscription;
import com.subscriptionservice.model.User;
import com.subscriptionservice.repository.SubscriptionRepository;
import com.subscriptionservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SubscriptionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    private User testUser;
    private SubscriptionDto testSubscriptionDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setFirstName("testuser");
        testUser.setLastName("testuser");
        testUser.setMiddleName("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPhone("+79282589980");
        testUser = userRepository.save(testUser);

        testSubscriptionDto = new SubscriptionDto();
        testSubscriptionDto.setServiceName("Test Service");
        testSubscriptionDto.setPrice(new BigDecimal("9.99"));
        testSubscriptionDto.setStartDate(OffsetDateTime.now());
        testSubscriptionDto.setEndDate(OffsetDateTime.now().plusMonths(1));
        testSubscriptionDto.setPlan("testplan");
        testSubscriptionDto.setUserId(testUser.getId());
    }

    @Test
    void createSubscription_ShouldReturnCreatedSubscription() throws Exception {
        mockMvc.perform(post("/api/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSubscriptionDto)))
                .andExpect(status().isCreated());
    }

    @Test
    void getSubscriptionById_ShouldReturnSubscription() throws Exception {
        Subscription subscription = objectMapper.convertValue(testSubscriptionDto, Subscription.class);
        subscription.setUser(testUser);
        Long subscriptionId = subscriptionRepository.save(subscription).getId();

        mockMvc.perform(get("/api/subscriptions/{id}", subscriptionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceName").value(testSubscriptionDto.getServiceName()))
                .andExpect(jsonPath("$.price").value(testSubscriptionDto.getPrice()));
    }

    @Test
    void updateSubscription_ShouldReturnUpdatedSubscription() throws Exception {        
        Subscription subscription = objectMapper.convertValue(testSubscriptionDto, Subscription.class);
        subscription.setUser(testUser);
        Long subscriptionId = subscriptionRepository.save(subscription).getId();

        testSubscriptionDto.setServiceName("Updated Service");
        testSubscriptionDto.setPrice(new BigDecimal("19.99"));

        mockMvc.perform(put("/api/subscriptions/{id}", subscriptionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testSubscriptionDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.serviceName").value("Updated Service"))
                .andExpect(jsonPath("$.price").value(19.99));
    }

    @Test
    void deleteSubscription_ShouldReturnNoContent() throws Exception {   
        Subscription subscription = objectMapper.convertValue(testSubscriptionDto, Subscription.class);
        subscription.setUser(testUser);
        Long subscriptionId = subscriptionRepository.save(subscription).getId();

        mockMvc.perform(delete("/api/subscriptions/{id}", subscriptionId))
                .andExpect(status().isNoContent());
    }

    @Test
    void getSubscriptionsByUserId_ShouldReturnUserSubscriptions() throws Exception {
        Subscription subscription = objectMapper.convertValue(testSubscriptionDto, Subscription.class);
        subscription.setUser(testUser);
        subscriptionRepository.save(subscription).getId();

        mockMvc.perform(get("/api/subscriptions/user/{userId}", testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].serviceName").value(testSubscriptionDto.getServiceName()))
                .andExpect(jsonPath("$[0].price").value(testSubscriptionDto.getPrice()));
    }

    @Test
    void getTopSubscriptions_ShouldReturnTopSubscriptions() throws Exception {
        for (int i = 0; i < 3; i++) {
            testSubscriptionDto.setServiceName("Service " + i);
            Subscription subscription = objectMapper.convertValue(testSubscriptionDto, Subscription.class);
            subscription.setUser(testUser);
            subscriptionRepository.save(subscription).getId();
        }

        mockMvc.perform(get("/api/subscriptions/top"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
} 