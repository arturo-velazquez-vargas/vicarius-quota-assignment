package com.vicarius.assignment.service.strategies;

import com.vicarius.assignment.dto.UserDTO;
import com.vicarius.assignment.exception.ResourceNotFoundException;
import com.vicarius.assignment.model.mysql.User;
import com.vicarius.assignment.repository.mysql.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * This class implements the UserFetchingStrategy interface for Mysql.
 * It provides methods to interact with Elasticsearch for user-related CRUD operations.
 * The strategy pattern is used to switch between different data sources (e.g., MySQL, Elasticsearch)
 * based on the time of day.
 */
@Service
public class MySQLUserFetchingStrategy implements UserFetchingStrategy {

    @Autowired
    @Qualifier("mysqlUserRepository")
    private UserRepository userRepository;

    @Override
    public UserDTO getUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .lastLoginTimeUtc(user.getLastLoginTimeUtc())
                .build();
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO createUser(UserDTO user) {
        user.setId(UUID.randomUUID().toString());
        User userCreated = userRepository.save(convertToEntity(user));
        return convertToDTO(userCreated);
    }

    @Override
    public UserDTO updateUser(String id, UserDTO userDetails) {
        // Fetch existing user data based on time-dependent data source
        UserDTO existingUserDTO = getUser(id);

        // Convert DTO back to User entity for updates
        User existingUser = convertToEntity(existingUserDTO);

        // Apply updates to the existing User entity
        if (userDetails.getFirstName() != null) {
            existingUser.setFirstName(userDetails.getFirstName());
        }
        if (userDetails.getLastName() != null) {
            existingUser.setLastName(userDetails.getLastName());
        }
        if (userDetails.getLastLoginTimeUtc() != null) {
            existingUser.setLastLoginTimeUtc(userDetails.getLastLoginTimeUtc());
        }

        // Save the updated user to the repository (MySQL)
        User updatedUser = userRepository.save(existingUser);

        // Return the updated user as DTO
        return convertToDTO(updatedUser);
    }

    @Override
    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        userRepository.delete(user);
    }


    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .lastLoginTimeUtc(user.getLastLoginTimeUtc())
                .build();
    }

    private User convertToEntity(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .firstName(userDTO.getFirstName())
                .lastName(userDTO.getLastName())
                .lastLoginTimeUtc(userDTO.getLastLoginTimeUtc())
                .build();
    }
}
