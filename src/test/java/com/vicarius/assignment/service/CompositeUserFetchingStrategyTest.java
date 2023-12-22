package com.vicarius.assignment.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.vicarius.assignment.dto.UserDTO;
import com.vicarius.assignment.service.strategies.ElasticsearchUserFetchingStrategy;
import com.vicarius.assignment.service.strategies.MySQLUserFetchingStrategy;
import com.vicarius.assignment.service.strategies.UserFetchingStrategy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CompositeUserFetchingStrategyTest {

    @Mock
    private MySQLUserFetchingStrategy mySQLStrategy;

    @Mock
    private ElasticsearchUserFetchingStrategy elasticsearchStrategy;

    @InjectMocks
    private CompositeUserFetchingStrategy compositeStrategy;

    // Mock the isDaytime method
    private void mockIsDaytime(boolean isDaytime) {
        try {
            CompositeUserFetchingStrategy spyCompositeStrategy = spy(compositeStrategy);
            doReturn(isDaytime).when(spyCompositeStrategy).isDaytime();
            compositeStrategy = spyCompositeStrategy;
        } catch (Exception e) {
            throw new RuntimeException("Error mocking isDaytime", e);
        }
    }


    @Test
    void getAllUsers_ShouldUseMySQLStrategyDuringDaytime() {
        mockIsDaytime(true);
        testGetAllUsersStrategy(mySQLStrategy);
    }

    @Test
    void getAllUsers_ShouldUseElasticsearchStrategyDuringNighttime() {
        mockIsDaytime(false);
        testGetAllUsersStrategy(elasticsearchStrategy);
    }

    private void testGetAllUsersStrategy(UserFetchingStrategy strategy) {
        List<UserDTO> expectedUsers = Collections.singletonList(UserDTO.builder()
                .id("user1")
                .firstName("Arturo")
                .lastName("Velazquez")
                .build());
        when(strategy.getAllUsers()).thenReturn(expectedUsers);

        List<UserDTO> result = compositeStrategy.getAllUsers();

        assertEquals(expectedUsers, result);
        verify(strategy).getAllUsers();
    }

    @Test
    void updateUser_ShouldUseMySQLStrategyDuringDaytime() {
        mockIsDaytime(true);
        testUpdateUserStrategy(mySQLStrategy);
    }

    @Test
    void updateUser_ShouldUseElasticsearchStrategyDuringNighttime() {
        mockIsDaytime(false);
        testUpdateUserStrategy(elasticsearchStrategy);
    }

    private void testUpdateUserStrategy(UserFetchingStrategy strategy) {
        String userId = "user1";
        UserDTO userDetails = UserDTO.builder()
                .id(userId)
                .firstName("Arturo")
                .lastName("Velazquez")
                .build();
        UserDTO updatedUser = UserDTO.builder()
                .id(userId)
                .firstName("Carlos")
                .lastName("Velazquez")
                .build();
        when(strategy.updateUser(userId, userDetails)).thenReturn(updatedUser);

        UserDTO result = compositeStrategy.updateUser(userId, userDetails);

        assertEquals(updatedUser, result);
        verify(strategy).updateUser(userId, userDetails);
    }

    @Test
    void deleteUser_ShouldUseMySQLStrategyDuringDaytime() {
        mockIsDaytime(true);
        testDeleteUserStrategy(mySQLStrategy);
    }

    @Test
    void deleteUser_ShouldUseElasticsearchStrategyDuringNighttime() {
        mockIsDaytime(false);
        testDeleteUserStrategy(elasticsearchStrategy);
    }

    private void testDeleteUserStrategy(UserFetchingStrategy strategy) {
        String userId = "user1";
        doNothing().when(strategy).deleteUser(userId);

        compositeStrategy.deleteUser(userId);

        verify(strategy).deleteUser(userId);
    }
}
