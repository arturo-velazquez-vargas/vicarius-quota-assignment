package com.vicarius.assignment.service;

import com.vicarius.assignment.dto.UserDTO;
import com.vicarius.assignment.service.strategies.ElasticsearchUserFetchingStrategy;
import com.vicarius.assignment.service.strategies.MySQLUserFetchingStrategy;
import com.vicarius.assignment.service.strategies.UserFetchingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;


/**
 * The CompositeUserFetchingStrategy class uses the Composite design pattern along with the Strategy pattern
 * to determine which data source to use for user data retrieval based on the time of day. It selects between
 * the MySQLUserFetchingStrategy and ElasticsearchUserFetchingStrategy as data sources depending on whether
 * it is daytime or nighttime.
 */
@Service
@Slf4j
public class CompositeUserFetchingStrategy implements UserFetchingStrategy {

    @Autowired
    private MySQLUserFetchingStrategy mySQLStrategy;

    @Autowired
    private ElasticsearchUserFetchingStrategy elasticsearchStrategy;

    @Override
    public UserDTO getUser(String userId) {
        return getStrategy().getUser(userId);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return getStrategy().getAllUsers();
    }

    @Override
    public UserDTO createUser(UserDTO user) {
        return getStrategy().createUser(user);
    }

    @Override
    public UserDTO updateUser(String userId, UserDTO userDetails) {
        return getStrategy().updateUser(userId, userDetails);
    }

    @Override
    public void deleteUser(String userId) {
        getStrategy().deleteUser(userId);
    }

    private UserFetchingStrategy getStrategy() {
        boolean isDayTime = isDaytime();
        log.info("isDayTime {} ", isDayTime);
        return isDayTime ? mySQLStrategy : elasticsearchStrategy;
    }

    public boolean isDaytime() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LocalTime time = now.toLocalTime();
        return !time.isBefore(LocalTime.of(9, 0)) && time.isBefore(LocalTime.of(17, 0));
    }
}
