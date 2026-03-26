package com.ezyenglish.auth.service;

import com.ezyenglish.auth.dto.AuthResponse;
import com.ezyenglish.auth.dto.SigninRequest;
import com.ezyenglish.auth.dto.SignupRequest;
import com.ezyenglish.auth.dto.UserResponse;
import com.ezyenglish.auth.dto.ProfileUpdateRequest;
import com.ezyenglish.auth.model.Role;
import com.ezyenglish.auth.model.User;
import com.ezyenglish.auth.repository.UserRepository;
import com.ezyenglish.auth.security.JwtUtils;
import com.ezyenglish.auth.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final String UPLOAD_DIR = "uploads/profiles/";

    public AuthResponse signup(SignupRequest request) {
        log.info("Processing signup for username: {}, email: {}", request.getUsername(), request.getEmail());

        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username already exists: {}", request.getUsername());
            throw new RuntimeException("Username is already taken");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already exists: {}", request.getEmail());
            throw new RuntimeException("Email is already in use");
        }

        Set<Role> roles = new HashSet<>();
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            roles.add(Role.STUDENT);
            log.info("No roles specified, defaulting to STUDENT");
        } else {
            request.getRoles().forEach(role -> {
                switch (role.toLowerCase()) {
                    case "admin":
                        roles.add(Role.ADMIN);
                        break;
                    case "teacher":
                        roles.add(Role.TEACHER);
                        break;
                    default:
                        roles.add(Role.STUDENT);
                }
            });
            log.info("Assigned roles: {}", roles);
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .roles(roles)
                .build();

        try {
            userRepository.save(user);
            log.info("User saved successfully: {}", request.getUsername());
        } catch (Exception e) {
            log.error("Error saving user: ", e);
            throw new RuntimeException("Failed to create user: " + e.getMessage());
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateToken((UserDetailsImpl) authentication.getPrincipal());

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Set<String> roleStrings = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority().replace("ROLE_", ""))
                    .collect(Collectors.toSet());

            log.info("Signup completed successfully for user: {}", request.getUsername());
            return new AuthResponse(jwt, userDetails.getId(), userDetails.getUsername(),
                    userDetails.getEmail(), roleStrings);
        } catch (Exception e) {
            log.error("Error during authentication after signup: ", e);
            throw new RuntimeException("Failed to authenticate after signup: " + e.getMessage());
        }
    }

    public AuthResponse signin(SigninRequest request) {
        log.info("Processing signin for username: {}", request.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateToken((UserDetailsImpl) authentication.getPrincipal());

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Set<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority().replace("ROLE_", ""))
                    .collect(Collectors.toSet());

            log.info("Signin completed successfully for user: {}", request.getUsername());
            return new AuthResponse(jwt, userDetails.getId(), userDetails.getUsername(),
                    userDetails.getEmail(), roles);
        } catch (Exception e) {
            log.error("Signin failed for username: {}, error: ", request.getUsername(), e);
            throw e;
        }
    }

    public UserResponse getCurrentUser(String username) {
        log.info("Fetching profile for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
        return userService.mapToResponse(user);
    }

    public UserResponse updateProfile(String username, User updateRequest, MultipartFile photo) throws IOException {
        log.info("Updating profile multipart for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (updateRequest.getFirstName() != null) user.setFirstName(updateRequest.getFirstName());
        if (updateRequest.getLastName() != null) user.setLastName(updateRequest.getLastName());
        if (updateRequest.getPhone() != null) user.setPhone(updateRequest.getPhone());
        if (updateRequest.getIdCardNo() != null) user.setIdCardNo(updateRequest.getIdCardNo());
        if (updateRequest.getAddress() != null) user.setAddress(updateRequest.getAddress());
        if (updateRequest.getCity() != null) user.setCity(updateRequest.getCity());
        if (updateRequest.getPostalCode() != null) user.setPostalCode(updateRequest.getPostalCode());
        if (updateRequest.getCountry() != null) user.setCountry(updateRequest.getCountry());

        if (photo != null && !photo.isEmpty()) {
            String photoUrl = saveFile(photo);
            user.setProfileImageUrl(photoUrl);
        }

        userRepository.save(user);
        return userService.mapToResponse(user);
    }

    public UserResponse updateProfileFromJson(String username, ProfileUpdateRequest updateRequest) {
        log.info("Updating profile JSON for user: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        if (updateRequest.getFirstName() != null) user.setFirstName(updateRequest.getFirstName());
        if (updateRequest.getLastName() != null) user.setLastName(updateRequest.getLastName());
        if (updateRequest.getPhone() != null) user.setPhone(updateRequest.getPhone());
        if (updateRequest.getIdCardNo() != null) user.setIdCardNo(updateRequest.getIdCardNo());
        if (updateRequest.getAddress() != null) user.setAddress(updateRequest.getAddress());
        if (updateRequest.getCity() != null) user.setCity(updateRequest.getCity());
        if (updateRequest.getPostalCode() != null) user.setPostalCode(updateRequest.getPostalCode());
        if (updateRequest.getCountry() != null) user.setCountry(updateRequest.getCountry());
        
        // Handling base64 image or direct URL
        if (updateRequest.getProfileImageUrl() != null) {
            String imageUrl = updateRequest.getProfileImageUrl();
            if (imageUrl.startsWith("data:image")) {
                try {
                    String base64Image = imageUrl.split(",")[1];
                    byte[] imageBytes = java.util.Base64.getDecoder().decode(base64Image);
                    String extension = imageUrl.split(";")[0].split("/")[1];
                    String fileName = UUID.randomUUID().toString() + "." + extension;
                    
                    Path uploadPath = Paths.get(UPLOAD_DIR);
                    if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
                    
                    Files.write(uploadPath.resolve(fileName), imageBytes);
                    user.setProfileImageUrl("/uploads/profiles/" + fileName);
                } catch (Exception e) {
                    log.error("Failed to save base64 image: ", e);
                    // Fallback to storing the raw string if decoding fails
                    user.setProfileImageUrl(imageUrl);
                }
            } else {
                user.setProfileImageUrl(imageUrl);
            }
        }

        userRepository.save(user);
        return userService.mapToResponse(user);
    }

    private String saveFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName != null && originalFileName.contains(".") 
                ? originalFileName.substring(originalFileName.lastIndexOf(".")) : "";
        String newFileName = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(newFileName);
        
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/profiles/" + newFileName;
    }
}
