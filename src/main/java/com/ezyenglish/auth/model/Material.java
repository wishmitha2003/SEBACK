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
@Document(collection = "materials")
public class Material {
    @Id
    private String id;
    private String title;
    private String type; // e.g., MULTI or the main extension
    private List<String> fileUrls; // List of paths to uploaded files
    private String className;
    private String uploadedBy;
    private LocalDateTime uploadedOn;
    private String status; // Published, Draft
}
