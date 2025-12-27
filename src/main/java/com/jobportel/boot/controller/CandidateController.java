package com.jobportel.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.jobportel.boot.model.Candidate;
import com.jobportel.boot.repository.CandidateRepository;

@RestController
@RequestMapping("/api/candidate")
@CrossOrigin(origins = "http://localhost:3000")
public class CandidateController {

    @Autowired
    private CandidateRepository candidateRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Candidate candidate) {

        // 1. Check if Email exists
        if (candidateRepository.findByEmail(candidate.getEmail().trim()).isPresent()) {
            // Returning a 409 Conflict with a specific message
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Already you wrote the exam with this Email!");
        }

        // 2. Check if Phone number exists
        if (candidateRepository.findByPhone(candidate.getPhone().trim()).isPresent()) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Already you wrote the exam with this Mobile Number!");
        }

        // 3. Save and return the candidate
        Candidate savedCandidate = candidateRepository.save(candidate);
        return ResponseEntity.ok(savedCandidate);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidate> getCandidate(@PathVariable("id") Long id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Candidate not found"));
        return ResponseEntity.ok(candidate);
    }
}