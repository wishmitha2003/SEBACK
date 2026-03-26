package com.ezyenglish.auth.service;

import com.ezyenglish.auth.model.Branch;
import com.ezyenglish.auth.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BranchService {

    private final BranchRepository repository;

    public List<Branch> getAllBranches() {
        log.info("Fetching all branches");
        return repository.findAll();
    }

    public Branch createBranch(Branch branch) {
        log.info("Creating new branch: {}", branch.getName());
        return repository.save(branch);
    }

    public void deleteBranch(String id) {
        log.info("Deleting branch: {}", id);
        repository.deleteById(id);
    }

    public Branch updateBranch(String id, Branch branch) {
        log.info("Updating branch: {}", id);
        if (!repository.existsById(id)) {
            throw new RuntimeException("Branch not found with id: " + id);
        }
        branch.setId(id);
        return repository.save(branch);
    }
}
