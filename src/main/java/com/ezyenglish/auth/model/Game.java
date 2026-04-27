package com.ezyenglish.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "games")
public class Game {
    @Id
    private String id;
    
    private String title;
    private String description;
    
    private GameType gameType;
    private AgeGroup ageGroup;
    
    private List<Question> questions;
    
    private Integer difficultyLevel;
    private Integer maxScore;
    private Integer timeLimit; // in seconds
    
    private String createdBy; // Teacher's username
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
