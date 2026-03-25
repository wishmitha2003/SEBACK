package com.ezyenglish.auth.service;

import com.ezyenglish.auth.model.LanguageClass;
import com.ezyenglish.auth.repository.LanguageClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LanguageClassService {

    private final LanguageClassRepository repository;

    public List<LanguageClass> getAllClasses() {
        log.info("Fetching all classes");
        return repository.findAll();
    }

    public LanguageClass createClass(LanguageClass languageClass) {
        log.info("Creating new class: {}", languageClass.getName());
        return repository.save(languageClass);
    }

    public void deleteClass(String id) {
        log.info("Deleting class with ID: {}", id);
        repository.deleteById(id);
    }
}
