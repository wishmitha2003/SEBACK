package com.ezyenglish.auth.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "class")
public class LanguageClass {

    @Id
    private String id;

    private String name;
    private String teacher;
    private Integer studentCount;
    private String schedule;
    private String branch;
    private String type; // online or physical
    private String status;
}
