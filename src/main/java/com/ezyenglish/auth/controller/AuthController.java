package com.ezyenglish.auth.controller;

import com.ezyenglish.auth.dto.*;
import com.ezyenglish.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600, allowCredentials = "true")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        try {
            log.info("Signup request received for username: {}", request.getUsername());
            AuthResponse response = authService.signup(request);
            log.info("Signup successful for username: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Signup failed for username: {}, error: {}", request.getUsername(), e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        } catch (Exception e) {
            log.error("Unexpected error during signup: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@Valid @RequestBody SigninRequest request) {
        try {
            log.info("Signin request received for username: {}", request.getUsername());
            AuthResponse response = authService.signin(request);
            log.info("Signin successful for username: {}", request.getUsername());
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for username: {}", request.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid username or password", HttpStatus.UNAUTHORIZED.value()));
        } catch (Exception e) {
            log.error("Signin failed for username: {}, error: ", request.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An error occurred during signin: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Not authenticated", HttpStatus.UNAUTHORIZED.value()));
        }
        try {
            UserResponse response = authService.getCurrentUser(userDetails.getUsername());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND.value()));
        }
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProfileUpdateRequest updateRequest) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Not authenticated", HttpStatus.UNAUTHORIZED.value()));
        }

        try {
            UserResponse response = authService.updateProfileFromJson(userDetails.getUsername(), updateRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Profile update failed: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    // Keep multipart version on a different path or with distinct params if needed later
    @PutMapping(value = "/update-profile-photo", consumes = {"multipart/form-data"})
    public ResponseEntity<?> updateProfilePhoto(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute com.ezyenglish.auth.model.User profileData,
            @RequestParam(value = "photo", required = false) MultipartFile photo) {
        
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Not authenticated", HttpStatus.UNAUTHORIZED.value()));
        }

        try {
            UserResponse response = authService.updateProfile(userDetails.getUsername(), profileData, photo);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Failed to upload photo: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }
}
