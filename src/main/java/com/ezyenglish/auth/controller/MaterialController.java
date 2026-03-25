package com.ezyenglish.auth.controller;

import com.ezyenglish.auth.model.Material;
import com.ezyenglish.auth.service.MaterialService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class MaterialController {

    private final MaterialService service;

    @GetMapping
    public ResponseEntity<List<Material>> getAllMaterials() {
        log.info("Request received to fetch all materials");
        return ResponseEntity.ok(service.getAllMaterials());
    }

    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<Material> createMaterial(
            @RequestPart(name = "material") Material material,
            @RequestPart(name = "files") List<MultipartFile> files) {
        log.info("Request received to upload material with {} files", (files != null ? files.size() : 0));
        try {
            Material created = service.createMaterial(material, files);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IOException e) {
            log.error("Failed to upload material: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMaterial(@PathVariable(name = "id") String id) {
        log.info("Request received to delete material: {}", id);
        service.deleteMaterial(id);
        return ResponseEntity.noContent().build();
    }
}
