package com.ezyenglish.auth.controller;

import com.ezyenglish.auth.dto.AuthResponse;
import com.ezyenglish.auth.dto.ErrorResponse;
import com.ezyenglish.auth.dto.SigninRequest;
import com.ezyenglish.auth.dto.SignupRequest;
import com.ezyenglish.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

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
}
