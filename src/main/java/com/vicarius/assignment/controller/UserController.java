package com.vicarius.assignment.controller;


import com.vicarius.assignment.dto.UserDTO;
import com.vicarius.assignment.service.QuotaService;
import com.vicarius.assignment.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private QuotaService quotaService;

    @GetMapping("/consumeQuota/{userId}")
    public ResponseEntity<String> consumeQuota(@PathVariable String userId) {
        if (!quotaService.consumeQuota(userId)) {
            return ResponseEntity.status(429).body("Quota exceeded for user: " + userId);
        }

        return ResponseEntity.ok("Request processed successfully for user: " + userId);
    }

    @GetMapping("/usersQuota")
    public ResponseEntity<List<UserDTO>> getUsersQuota() {
        List<UserDTO> usersQuota = quotaService.getUsersQuota();
        if (usersQuota.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(usersQuota);
    }

    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String id, @RequestBody UserDTO userDetails) {
        return ResponseEntity.ok(userService.updateUser(id, userDetails));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
