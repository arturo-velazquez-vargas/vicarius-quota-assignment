package com.vicarius.assignment.service.strategies;

import com.vicarius.assignment.dto.UserDTO;
import com.vicarius.assignment.model.mysql.User;

import java.util.List;


/**
 * The UserFetchingStrategy interface defines methods for fetching, creating, updating, and deleting users.
 * Implementations of this interface can provide different strategies for accessing user data, such as
 * from a MySQL database, Elasticsearch, or other sources.
 */
public interface UserFetchingStrategy {
    UserDTO getUser(String userId);

    List<UserDTO> getAllUsers();

    UserDTO createUser(UserDTO user);

    UserDTO updateUser(String id, UserDTO userDetails);

    void deleteUser(String userId);
}
