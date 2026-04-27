package com.ezyenglish.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    private String id;
    private String englishWord;
    private String correctAnswer;
    private List<String> options;
}
