package com.subscriptionservice.mapper;

import com.subscriptionservice.dto.SubscriptionDto;
import com.subscriptionservice.model.Subscription;
import com.subscriptionservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    Subscription toEntity(SubscriptionDto dto, User user);

    @Mapping(target = "userId", source = "user.id")
    SubscriptionDto toDto(Subscription subscription);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    void updateEntityFromDto(SubscriptionDto dto, @MappingTarget Subscription subscription, User user);
} 