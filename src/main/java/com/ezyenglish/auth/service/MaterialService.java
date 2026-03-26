package com.ezyenglish.auth.service;

import com.ezyenglish.auth.model.Material;
import com.ezyenglish.auth.repository.MaterialRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialService {

    private final MaterialRepository repository;
    private final String UPLOAD_DIR = "uploads/";

    public List<Material> getAllMaterials() {
        log.info("Fetching all materials");
        return repository.findAll();
    }

    public Material createMaterial(Material material, List<MultipartFile> files) throws IOException {
        log.info("Creating new material: {} with {} files", material.getTitle(), (files != null ? files.size() : 0));
        
        List<String> fileUrls = new ArrayList<>();
        
        if (files != null && !files.isEmpty()) {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                
                String originalFileName = file.getOriginalFilename();
                String extension = originalFileName != null && originalFileName.contains(".") ? originalFileName.substring(originalFileName.lastIndexOf(".")) : "";
                String newFileName = UUID.randomUUID().toString() + extension;
                Path filePath = uploadPath.resolve(newFileName);
                
                Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
                fileUrls.add("/uploads/" + newFileName);
            }
            material.setFileUrls(fileUrls);
            
            // Set main type based on first file or 'MULTI'
            if (fileUrls.size() == 1) {
                String firstFile = fileUrls.get(0);
                String extension = firstFile.substring(firstFile.lastIndexOf(".") + 1).toUpperCase();
                material.setType(extension);
            } else if (fileUrls.size() > 1) {
                material.setType("MULTI");
            }
        }

        if (material.getUploadedOn() == null) {
            material.setUploadedOn(LocalDateTime.now());
        }
        return repository.save(material);
    }

    public void deleteMaterial(String id) {
        log.info("Deleting material with ID: {}", id);
        repository.deleteById(id);
    }
}
