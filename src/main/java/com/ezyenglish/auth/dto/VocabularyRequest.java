package com.ezyenglish.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VocabularyRequest {
    private String word;
    private String meaning;
    private String example;
    private String ageSection; // 1-5, 6-10, 11-15, 16-20, 20+
}