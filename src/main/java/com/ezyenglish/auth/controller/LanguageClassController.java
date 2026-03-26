package com.ezyenglish.auth.controller;

import com.ezyenglish.auth.model.LanguageClass;
import com.ezyenglish.auth.service.LanguageClassService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/classes")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600, allowCredentials = "true")
public class LanguageClassController {

    private final LanguageClassService classService;

    @GetMapping
    public ResponseEntity<List<LanguageClass>> getAllClasses() {
        log.info("Received request to fetch all classes");
        try {
            List<LanguageClass> classes = classService.getAllClasses();
            return ResponseEntity.ok(classes);
        } catch (Exception e) {
            log.error("Failed to fetch classes: ", e);
            throw e;
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LanguageClass> createClass(@RequestBody LanguageClass languageClass) {
        log.info("Received request to create class: {}", languageClass.getName());
        try {
            LanguageClass created = classService.createClass(languageClass);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            log.error("Failed to create class: ", e);
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteClass(@PathVariable("id") String id) {
        log.info("Received request to delete class with ID: {}", id);
        try {
            classService.deleteClass(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to delete class: ", e);
            throw e;
        }
    }
}
