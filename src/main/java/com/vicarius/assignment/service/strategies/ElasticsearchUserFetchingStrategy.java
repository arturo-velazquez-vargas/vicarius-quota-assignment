package com.vicarius.assignment.service.strategies;

import com.vicarius.assignment.dto.UserDTO;
import com.vicarius.assignment.exception.ResourceNotFoundException;
import com.vicarius.assignment.model.elasticsearch.User;
import com.vicarius.assignment.repository.elasticsearch.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.UUID;
import java.util.stream.StreamSupport;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class implements the UserFetchingStrategy interface for Elasticsearch.
 * It provides methods to interact with Elasticsearch for user-related CRUD operations.
 * The strategy pattern is used to switch between different data sources (e.g., MySQL, Elasticsearch)
 * based on the time of day.
 */
@Service
public class ElasticsearchUserFetchingStrategy implements UserFetchingStrategy {

    @Autowired
    @Qualifier("elasticUserRepository")
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
        Iterable<User> users = userRepository.findAll();
        return StreamSupport.stream(users.spliterator(), false)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO createUser(UserDTO user) {
        user.setId(UUID.randomUUID().toString());
        User userCreated = userRepository.save(convertToEntity(user));
        return convertToDTO(userCreated);
    }

    @Override
    public UserDTO updateUser(String id, UserDTO userDetails) {
        UserDTO existingUserDTO = getUser(id);

        User existingUser = convertToEntity(existingUserDTO);

        if (userDetails.getFirstName() != null) {
            existingUser.setFirstName(userDetails.getFirstName());
        }
        if (userDetails.getLastName() != null) {
            existingUser.setLastName(userDetails.getLastName());
        }
        if (userDetails.getLastLoginTimeUtc() != null) {
            existingUser.setLastLoginTimeUtc(userDetails.getLastLoginTimeUtc());
        }

        User updatedUser = userRepository.save(existingUser);

        return convertToDTO(updatedUser);
    }

    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
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
                .lastLoginTimeUtc(userDTO.getLastLoginTimeUtc().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .build();
    }
}
