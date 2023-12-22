package com.vicarius.assignment.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.vicarius.assignment.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private CompositeUserFetchingStrategy userStrategy;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getUser_ShouldDelegateCall() {
        String userId = "user1";
        UserDTO mockedUser = UserDTO.builder()
                .id(userId)
                .firstName("Arturo")
                .lastName("Velazquez")
                .build();

        when(userStrategy.getUser(userId)).thenReturn(mockedUser);

        UserDTO result = userService.getUser(userId);

        assertEquals(mockedUser, result);
        verify(userStrategy).getUser(userId);
    }

    @Test
    void getAllUsers_ShouldDelegateCall() {
        List<UserDTO> mockedUsers = Arrays.asList(UserDTO.builder()
                        .id("user1")
                        .firstName("Arturo")
                        .lastName("Velazquez")
                        .build(),
                UserDTO.builder()
                        .id("user2")
                        .firstName("Carlos")
                        .lastName("Velazquez")
                        .build()
        );
        when(userStrategy.getAllUsers()).thenReturn(mockedUsers);

        List<UserDTO> result = userService.getAllUsers();

        assertEquals(mockedUsers, result);
        verify(userStrategy).getAllUsers();
    }

    @Test
    void createUser_ShouldDelegateCall() {
        UserDTO newUser = UserDTO.builder()
                .firstName("Arturo")
                .lastName("Velazquez")
                .build();

        UserDTO createdUser = UserDTO.builder()
                .id("user3")
                .firstName("Carlos")
                .lastName("Velazquez")
                .build();

        when(userStrategy.createUser(newUser)).thenReturn(createdUser);

        UserDTO result = userService.createUser(newUser);

        assertEquals(createdUser, result);
        verify(userStrategy).createUser(newUser);
    }

    @Test
    void updateUser_ShouldDelegateCall() {
        String userId = "user1";
        UserDTO updatedDetails = UserDTO.builder()
                .id(userId)
                .firstName("Carlos")
                .lastName("Velazquez")
                .build();
        when(userStrategy.updateUser(userId, updatedDetails)).thenReturn(updatedDetails);

        UserDTO result = userService.updateUser(userId, updatedDetails);

        assertEquals(updatedDetails, result);
        verify(userStrategy).updateUser(userId, updatedDetails);
    }

    @Test
    void deleteUser_ShouldDelegateCall() {
        String userId = "user1";
        userService.deleteUser(userId);

        verify(userStrategy).deleteUser(userId);
    }
}

