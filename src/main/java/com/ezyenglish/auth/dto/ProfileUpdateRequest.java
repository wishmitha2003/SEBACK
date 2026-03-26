package com.ezyenglish.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {
    private String firstName;
    private String lastName;
    private String phone;
    private String idCardNo;
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private String profileImageUrl; // Base64 or URL
}
