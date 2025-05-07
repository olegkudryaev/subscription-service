package com.subscriptionservice.mapper;

import com.subscriptionservice.dto.UserDto;
import com.subscriptionservice.dto.UserUpdateDto;
import com.subscriptionservice.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subscriptions", ignore = true)
    User toEntity(UserDto dto);

    UserDto toDto(User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "subscriptions", ignore = true)
    void updateEntityFromDto(UserUpdateDto dto, @MappingTarget User entity);
} 