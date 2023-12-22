package com.vicarius.assignment.service;

import com.vicarius.assignment.dto.UserDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuotaServiceTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private CompositeUserFetchingStrategy userStrategy;


    @InjectMocks
    private QuotaService quotaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void consumeQuota_NotExceeded() {
        String userId = "user1";
        when(userStrategy.getUser(userId)).thenReturn(new UserDTO());
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("quota:user1")).thenReturn("2");

        boolean result = quotaService.consumeQuota(userId);

        assertTrue(result);
        verify(valueOperations).increment("quota:user1", 1);
    }

    @Test
    void consumeQuota_Exceeded() {
        String userId = "user2";
        when(userStrategy.getUser(userId)).thenReturn(new UserDTO());
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("quota:user2")).thenReturn("5");

        boolean result = quotaService.consumeQuota(userId);

        assertFalse(result);
    }

    @Test
    void getUsersQuota() {
        List<UserDTO> users = new ArrayList<>();
        users.add(UserDTO.builder()
                .id("user1")
                .firstName("John")
                .lastName("Doe")
                .lastLoginTimeUtc(null) // or set a specific LocalDateTime if needed
                .build());
        users.add(UserDTO.builder()
                .id("user2")
                .firstName("Jane")
                .lastName("Doe")
                .lastLoginTimeUtc(null) // or set a specific LocalDateTime if needed
                .build());
        when(userStrategy.getAllUsers()).thenReturn(users);
        ValueOperations<String, String> valueOperations = mock(ValueOperations.class);
        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("quota:user1")).thenReturn("3");
        when(valueOperations.get("quota:user2")).thenReturn("2");

        List<UserDTO> result = quotaService.getUsersQuota();

        assertEquals(2, result.size());
        assertEquals(3, result.get(0).getQuota());
        assertEquals(2, result.get(1).getQuota());
    }
}
