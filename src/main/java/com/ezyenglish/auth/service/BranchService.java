package com.ezyenglish.auth.service;

import com.ezyenglish.auth.model.Branch;
import com.ezyenglish.auth.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository repository;
    private final String UPLOAD_DIR = "uploads/";

    public List<Branch> getAllBranches() {
        log.info("Fetching all branches");
        return repository.findAll();
    }

    public Branch createBranch(Branch branch, MultipartFile logo) throws IOException {
        log.info("Creating new branch: {}", branch.getName());
        if (logo != null && !logo.isEmpty()) {
            String logoUrl = saveFile(logo);
            branch.setLogoUrl(logoUrl);
        }
        return repository.save(branch);
    }

    public void deleteBranch(String id) {
        log.info("Deleting branch: {}", id);
        repository.deleteById(id);
    }

    public Branch updateBranch(String id, Branch branch, MultipartFile logo) throws IOException {
        log.info("Updating branch: {}", id);
        Branch existing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + id));
        
        branch.setId(id);
        if (logo != null && !logo.isEmpty()) {
            String logoUrl = saveFile(logo);
            branch.setLogoUrl(logoUrl);
        } else {
            branch.setLogoUrl(existing.getLogoUrl()); // Keep existing logo if none provided
        }
        
        return repository.save(branch);
    }

    private String saveFile(MultipartFile file) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName != null && originalFileName.contains(".") 
                ? originalFileName.substring(originalFileName.lastIndexOf(".")) : "";
        String newFileName = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(newFileName);
        
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return "/uploads/" + newFileName;
    }
}
