package com.vicarius.assignment.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.vicarius.assignment.dto.UserDTO;
import com.vicarius.assignment.exception.ResourceNotFoundException;
import com.vicarius.assignment.model.elasticsearch.User;
import com.vicarius.assignment.repository.elasticsearch.UserRepository;
import com.vicarius.assignment.service.strategies.ElasticsearchUserFetchingStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ElasticsearchUserFetchingStrategyTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ElasticsearchUserFetchingStrategy strategy;

    // Test setup
    private UserDTO userDTO;
    private User user;

    @BeforeEach
    void setUp() {
        LocalDateTime nowTruncated = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

        userDTO = UserDTO.builder()
                .id("1")
                .firstName("Arturo")
                .lastName("Velazquez")
                .lastLoginTimeUtc(nowTruncated)
                .build();

        user = User.builder()
                .id("1")
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .lastLoginTimeUtc(userDTO.getLastLoginTimeUtc().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build();
    }

    @Test
    void getUser_ShouldReturnUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDTO result = strategy.getUser(user.getId());

        assertNotNull(result);
        assertEquals(userDTO, result);
    }

    @Test
    void getUser_ShouldThrowExceptionWhenNotFound() {
        when(userRepository.findById("invalidId")).thenThrow(new ResourceNotFoundException("User not found"));

        assertThrows(ResourceNotFoundException.class, () -> strategy.getUser("invalidId"));
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<UserDTO> result = strategy.getAllUsers();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(userDTO, result.get(0));
    }

    @Test
    void createUser_ShouldCreateAndReturnUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO result = strategy.createUser(userDTO);

        assertNotNull(result);
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO updatedUserDTO = UserDTO.builder()
                .id(userDTO.getId())
                .firstName("Arturo")
                .lastName("Velazquez")
                .lastLoginTimeUtc(LocalDateTime.now())
                .build();

        UserDTO result = strategy.updateUser(user.getId(), updatedUserDTO);

        assertNotNull(result);
        assertEquals(updatedUserDTO.getFirstName(), result.getFirstName());
        assertEquals(updatedUserDTO.getLastName(), result.getLastName());
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

        assertDoesNotThrow(() -> strategy.deleteUser(user.getId()));
        verify(userRepository, times(1)).delete(user);
    }
}

