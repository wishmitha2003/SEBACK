package com.ezyenglish.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "vocabularies")
public class Vocabulary {
    @Id
    private String id;
    private String word;
    private String meaning;
    private String example; // Optional example sentence
    private String ageSection; // 1-5, 6-10, 11-15, 16-20, 20+
    private String addedBy; // Teacher's username
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}