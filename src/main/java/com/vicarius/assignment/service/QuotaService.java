package com.vicarius.assignment.service;

import com.vicarius.assignment.dto.UserDTO;
import com.vicarius.assignment.repository.mysql.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
public class QuotaService {

    private static final int MAX_REQUESTS = 5; // Max number of requests per user
    private static final int HOUR_T0_EXPIRE = 0;
    private static final int MINUTE_TO_EXPIRE = 5;
    private static final int SECOND_TO_EXPIRE = 0;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CompositeUserFetchingStrategy userStrategy;

    @Autowired
    @Qualifier("mysqlUserRepository")
    private UserRepository userRepository;


    public boolean consumeQuota(String userId) {
        // Validate if the user exists
        userStrategy.getUser(userId);

        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        String key = "quota:" + userId;
        String currentQuotaStr = ops.get(key);
        int currentQuota = currentQuotaStr != null ? Integer.parseInt(currentQuotaStr) : 0;

        log.info("Current quota for user {}: {}", userId, currentQuota);

        if (currentQuota >= MAX_REQUESTS) {
            log.info("Quota exceeded for user {}", userId);
            return false;
        }

        // If it's the first request, initialize the quota count and set the expiry
        if (currentQuota == 0) {
            log.info("Setting the quota to 1 for the user {}", userId);
            ops.set(key, "1");
            setExpiryTimeInSeconds(key);
        } else {
            ops.increment(key, 1);
        }

        log.info("Quota incremented for user {}", userId);
        return true;
    }


    public List<UserDTO> getUsersQuota() {
        // Fetch all users based on the current time-dependent data source
        List<UserDTO> users = userStrategy.getAllUsers();
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();

        // Check and set quota for each user
        List<UserDTO> usersWithQuota = users.parallelStream()
                .map(u -> attachUserQuota(u, ops))
                .collect(Collectors.toList());

        return usersWithQuota;
    }

    private UserDTO attachUserQuota(UserDTO userDTO, ValueOperations<String, String> ops) {
        try {
            String key = "quota:" + userDTO.getId();
            String quotaStr = ops.get(key);
            log.info("Quota for key {} ,  {}", key, quotaStr);
            int quota = quotaStr != null ? Integer.parseInt(quotaStr) : 0;
            userDTO.setQuota(quota);
        } catch (NumberFormatException e) {
            log.error("Error parsing quota for user {}: {}", userDTO.getId(), e.getMessage());
        }
        return userDTO;
    }

    private void setExpiryTimeInSeconds(String key) {
        long seconds = (HOUR_T0_EXPIRE * 3600L) + (MINUTE_TO_EXPIRE * 60L) + SECOND_TO_EXPIRE;
        stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
        log.info("Expiry for key '{}' set to {} seconds", key, seconds);
    }
}
