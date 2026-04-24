package com.ezyenglish.auth.service;

import com.ezyenglish.auth.model.LanguageClass;
import com.ezyenglish.auth.repository.LanguageClassRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public LanguageClass updateClass(String id, LanguageClass languageClass) {
        log.info("Updating class with ID: {}", id);
        Optional<LanguageClass> existingClass = repository.findById(id);
        if (existingClass.isPresent()) {
            LanguageClass classToUpdate = existingClass.get();
            
            // Update all fields from the request
            if (languageClass.getName() != null) {
                classToUpdate.setName(languageClass.getName());
            }
            if (languageClass.getTeacher() != null) {
                classToUpdate.setTeacher(languageClass.getTeacher());
            }
            if (languageClass.getStudentCount() != null) {
                classToUpdate.setStudentCount(languageClass.getStudentCount());
            }
            if (languageClass.getSchedule() != null) {
                classToUpdate.setSchedule(languageClass.getSchedule());
            }
            if (languageClass.getBranch() != null) {
                classToUpdate.setBranch(languageClass.getBranch());
            }
            if (languageClass.getType() != null) {
                classToUpdate.setType(languageClass.getType());
            }
            if (languageClass.getStatus() != null) {
                classToUpdate.setStatus(languageClass.getStatus());
            }
            if (languageClass.getFee() != null) {
                classToUpdate.setFee(languageClass.getFee());
            }
            
            return repository.save(classToUpdate);
        }
        throw new RuntimeException("Class not found with id: " + id);
    }

    public void deleteClass(String id) {
        log.info("Deleting class with ID: {}", id);
        repository.deleteById(id);
    }
}
