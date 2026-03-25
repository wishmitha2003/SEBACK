package com.ezyenglish.auth.service;

import com.ezyenglish.auth.dto.UserResponse;
import com.ezyenglish.auth.model.User;
import com.ezyenglish.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users from repository");
        List<User> users = userRepository.findAll();

        return users.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private UserResponse mapToResponse(User user) {
        String fullName = (user.getFirstName() != null ? user.getFirstName() : "") +
                (user.getLastName() != null ? " " + user.getLastName() : "");

        // Find the most appropriate role for display
        String roleDisplay = "Student";
        if (user.getRoles().contains(com.ezyenglish.auth.model.Role.ADMIN)) {
            roleDisplay = "Admin";
        } else if (user.getRoles().contains(com.ezyenglish.auth.model.Role.TEACHER)) {
            roleDisplay = "Teacher";
        }

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .fullName(fullName.trim())
                .phone(user.getPhone())
                .roles(user.getRoles()) // Still keep original roles
                .role(roleDisplay) // Add display role
                .status("Active")
                .build();
    }
}
