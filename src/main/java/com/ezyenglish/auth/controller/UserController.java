package com.ezyenglish.auth.controller;

import com.ezyenglish.auth.dto.UserResponse;
import com.ezyenglish.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600, allowCredentials = "true")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("Received request to fetch all users");
        try {
            List<UserResponse> users = userService.getAllUsers();
            log.info("Successfully fetched {} users", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Failed to fetch users: ", e);
            throw e;
        }
    }
}
