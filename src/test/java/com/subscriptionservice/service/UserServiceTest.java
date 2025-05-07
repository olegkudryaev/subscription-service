package com.subscriptionservice.service;

import com.subscriptionservice.dto.UserDto;
import com.subscriptionservice.dto.UserUpdateDto;
import com.subscriptionservice.mapper.UserMapper;
import com.subscriptionservice.model.User;
import com.subscriptionservice.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDto testUserDto;
    private UserUpdateDto testUserUpdateDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("testuser");
        testUser.setLastName("testuser");
        testUser.setMiddleName("testuser");
        testUser.setEmail("test@example.com");

        testUserDto = new UserDto();
        testUserDto.setFirstName("testuser");
        testUserDto.setLastName("testuser");
        testUserDto.setMiddleName("testuser");
        testUserDto.setEmail("test@example.com");

        testUserUpdateDto = new UserUpdateDto();
        testUserUpdateDto.setFirstName("testuser");
        testUserUpdateDto.setLastName("testuser");
        testUserUpdateDto.setMiddleName("testuser");
        testUserUpdateDto.setEmail("updated@example.com");
    }

    @Test
    void createUser_ShouldReturnUserId() {
        when(userRepository.existsByEmail(testUserDto.getEmail())).thenReturn(false);
        when(userMapper.toEntity(testUserDto)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        Long userId = userService.createUser(testUserDto);

        assertNotNull(userId);
        assertEquals(testUser.getId(), userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_WithDuplicateEmail_ShouldThrowException() {
        when(userRepository.existsByEmail(testUserDto.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> 
            userService.createUser(testUserDto)
        );
    }

    @Test
    void getUser_ShouldReturnUser() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        UserDto result = userService.getUser(testUser.getId());

        assertNotNull(result);
        assertEquals(testUserDto.getEmail(), result.getEmail());
    }

    @Test
    void getUser_WithNonExistentId_ShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            userService.getUser(1L)
        );
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(testUserUpdateDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        UserDto result = userService.updateUser(testUser.getId(), testUserUpdateDto);

        assertNotNull(result);
        verify(userMapper).updateEntityFromDto(testUserUpdateDto, testUser);
    }

    @Test
    void updateUser_WithNonExistentId_ShouldThrowException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () ->
            userService.updateUser(1L, testUserUpdateDto)
        );
    }

    @Test
    void updateUser_WithDuplicateEmail_ShouldThrowException() {
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(testUserUpdateDto.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
            userService.updateUser(testUser.getId(), testUserUpdateDto)
        );
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        when(userRepository.existsById(testUser.getId())).thenReturn(true);

        userService.deleteUser(testUser.getId());

        verify(userRepository).deleteById(testUser.getId());
    }

    @Test
    void deleteUser_WithNonExistentId_ShouldThrowException() {
        when(userRepository.existsById(anyLong())).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () ->
            userService.deleteUser(1L)
        );
    }
} 