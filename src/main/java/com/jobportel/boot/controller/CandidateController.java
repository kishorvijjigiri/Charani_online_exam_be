package com.jobportel.boot.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.jobportel.boot.model.Candidate;
import com.jobportel.boot.repository.CandidateRepository;

@RestController
@RequestMapping("/api/candidate")
@CrossOrigin(origins = "http://localhost:3000")
public class CandidateController {

    @Autowired
    private CandidateRepository candidateRepository;

    // Register candidate
    @PostMapping("/register")
    public Candidate register(@RequestBody Candidate candidate) {

        Optional<Candidate> byEmail =
                candidateRepository.findByEmail(candidate.getEmail());

        if (byEmail.isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        Optional<Candidate> byPhone =
                candidateRepository.findByPhone(candidate.getPhone());

        if (byPhone.isPresent()) {
            throw new RuntimeException("Phone already registered");
        }

        return candidateRepository.save(candidate);
    }

    // Get candidate details
    @GetMapping("/{id}")
    public Candidate getCandidate(@PathVariable("id") Long id) {
        return candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));
    }
}
