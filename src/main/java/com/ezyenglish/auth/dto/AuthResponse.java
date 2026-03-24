package com.ezyenglish.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;

    @Builder.Default
    private String type = "Bearer";

    private String id;
    private String username;
    private String email;
    private Set<String> roles;

    public AuthResponse(String token, String id, String username, String email, Set<String> roles) {
        this.token = token;
        this.type = "Bearer";
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
    }
}
