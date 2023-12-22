package com.vicarius.assignment.service;

import com.vicarius.assignment.dto.UserDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UserService {

    @Autowired
    private CompositeUserFetchingStrategy userStrategy;


    public UserDTO getUser(String userId) {
        return userStrategy.getUser(userId);
    }

    public List<UserDTO> getAllUsers() {
        return userStrategy.getAllUsers();
    }

    public UserDTO getUserById(String id) {
        return userStrategy.getUser(id);
    }

    public UserDTO createUser(UserDTO user) {
        return userStrategy.createUser(user);
    }

    public UserDTO updateUser(String id, UserDTO userDetails) {
        return userStrategy.updateUser(id, userDetails);
    }

    public void deleteUser(String id) {
        userStrategy.deleteUser(id);
    }
}
