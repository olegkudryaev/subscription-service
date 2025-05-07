package com.subscriptionservice.service;

import com.subscriptionservice.dto.UserDto;
import com.subscriptionservice.dto.UserUpdateDto;
import com.subscriptionservice.mapper.UserMapper;
import com.subscriptionservice.model.User;
import com.subscriptionservice.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public Long createUser(UserDto userDto) {
        checkUserDuplicate(userDto);
        User user = userMapper.toEntity(userDto);
        return userRepository.save(user).getId();
    }

    @Transactional(readOnly = true)
    public UserDto getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }

    @Transactional
    public UserDto updateUser(Long id, UserUpdateDto updateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        checkUserUpdateDto(updateDto, user);
        userMapper.updateEntityFromDto(updateDto, user);

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private void checkUserDuplicate(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        if (userRepository.existsByPhone(userDto.getPhone())) {
            throw new IllegalArgumentException("User with this phone number already exists");
        }
    }

    private void checkUserUpdateDto(UserUpdateDto updateDto, User user) {
        if (updateDto.getEmail() != null && !user.getEmail().equals(updateDto.getEmail()) 
                && userRepository.existsByEmail(updateDto.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        if (updateDto.getPhone() != null && !user.getPhone().equals(updateDto.getPhone()) 
                && userRepository.existsByPhone(updateDto.getPhone())) {
            throw new IllegalArgumentException("User with this phone number already exists");
        }
    }
} 