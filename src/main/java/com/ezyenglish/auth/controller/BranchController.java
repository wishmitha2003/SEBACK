package com.ezyenglish.auth.controller;

import com.ezyenglish.auth.model.Branch;
import com.ezyenglish.auth.service.BranchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/branches")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", maxAge = 3600, allowCredentials = "true")
public class BranchController {

    private final BranchService service;

    @GetMapping
    public ResponseEntity<List<Branch>> getAllBranches() {
        log.info("Request received to fetch all branches");
        return ResponseEntity.ok(service.getAllBranches());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Branch> createBranch(@RequestBody Branch branch) {
        log.info("Request received to create branch: {}", branch.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createBranch(branch));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBranch(@PathVariable String id) {
        log.info("Request received to delete branch: {}", id);
        service.deleteBranch(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Branch> updateBranch(@PathVariable String id, @RequestBody Branch branch) {
        log.info("Request received to update branch: {}", id);
        return ResponseEntity.ok(service.updateBranch(id, branch));
    }
}
