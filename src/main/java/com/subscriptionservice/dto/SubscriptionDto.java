package com.subscriptionservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
public class SubscriptionDto {

    @NotBlank(message = "Service name is required")
    private String serviceName;

    @NotBlank(message = "Plan is required")
    private String plan;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @NotNull(message = "Start date is required")
    private OffsetDateTime startDate;

    @NotNull(message = "End date is required")
    private OffsetDateTime endDate;

    @NotNull(message = "User ID is required")
    private Long userId;
} 