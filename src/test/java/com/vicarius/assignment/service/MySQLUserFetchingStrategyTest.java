package com.vicarius.assignment.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.vicarius.assignment.dto.UserDTO;
import com.vicarius.assignment.exception.ResourceNotFoundException;
import com.vicarius.assignment.model.mysql.User;
import com.vicarius.assignment.repository.mysql.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.vicarius.assignment.service.strategies.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MySQLUserFetchingStrategyTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MySQLUserFetchingStrategy strategy;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getUser_ShouldReturnUser_WhenUserExists() {
        String userId = "1";
        User user = new User(userId, "John", "Doe", LocalDateTime.now());
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDTO result = strategy.getUser(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(user.getFirstName(), result.getFirstName());
        assertEquals(user.getLastName(), result.getLastName());
        assertEquals(user.getLastLoginTimeUtc(), result.getLastLoginTimeUtc());
    }

    @Test
    public void getUser_ShouldThrowException_WhenUserNotFound() {
        String userId = "1";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> strategy.getUser(userId));
    }

    @Test
    public void getAllUsers_ShouldReturnAllUsers() {
        List<User> users = Arrays.asList(
                User.builder()
                        .id("1")
                        .firstName("Arturo")
                        .lastName("Velazquez")
                        .lastLoginTimeUtc(LocalDateTime.now())
                        .build(),
                User.builder()
                        .id("2")
                        .firstName("Carlos")
                        .lastName("Velazquez")
                        .lastLoginTimeUtc(LocalDateTime.now())
                        .build());

        when(userRepository.findAll()).thenReturn(users);

        List<UserDTO> result = strategy.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void createUser_ShouldReturnCreatedUser() {
        UserDTO newUserDTO = UserDTO.builder()
                .firstName("Alice")
                .lastName("Smith")
                .build();

        User newUser = new User(UUID.randomUUID().toString(), newUserDTO.getFirstName(), newUserDTO.getLastName(), null);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        UserDTO result = strategy.createUser(newUserDTO);

        assertNotNull(result);
        assertEquals(newUser.getId(), result.getId());
        assertEquals(newUser.getFirstName(), result.getFirstName());
        assertEquals(newUser.getLastName(), result.getLastName());
    }

    @Test
    public void updateUser_ShouldUpdateAndReturnUpdatedUser() {
        String userId = "1";
        User existingUser = User.builder()
                .id(userId)
                .firstName("Carlos")
                .lastName("Velazquez")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        UserDTO userDetails = UserDTO.builder()
                .firstName("Arturo")
                .lastName("Velazquez")
                .build();

        User updatedUser = new User(userId, userDetails.getFirstName(), userDetails.getLastName(), existingUser.getLastLoginTimeUtc());
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDTO result = strategy.updateUser(userId, userDetails);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(userDetails.getFirstName(), result.getFirstName());
        assertEquals(userDetails.getLastName(), result.getLastName());
        assertEquals(existingUser.getLastLoginTimeUtc(), result.getLastLoginTimeUtc());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void deleteUser_ShouldDeleteUser_WhenUserExists() {
        String userId = "1";
        User existingUser = User.builder()
                .firstName("Arturo")
                .lastName("Velazquez")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        strategy.deleteUser(userId);

        verify(userRepository).delete(existingUser);
    }

    @Test
    public void deleteUser_ShouldThrowException_WhenUserNotFound() {
        String userId = "1";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> strategy.deleteUser(userId));
    }

}
