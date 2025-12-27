package com.jobportel.boot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.jobportel.boot.model.Candidate;
import com.jobportel.boot.repository.CandidateRepository;

import java.io.IOException;

@RestController
@RequestMapping("/api/candidate")
@CrossOrigin(origins = "https://charanionlineexam.vercel.app")
public class CandidateController {

    @Autowired
    private CandidateRepository candidateRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(
        @ModelAttribute Candidate candidate, 
        @RequestParam(value = "resume", required = false) MultipartFile resumeFile
    ) {
        // 1. Check if Email exists
        if (candidateRepository.findByEmail(candidate.getEmail().trim()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists!");
        }

        // 2. Check if Phone exists
        if (candidateRepository.findByPhone(candidate.getPhone().trim()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Mobile Number already exists!");
        }

        // 3. Handle Resume (If uploaded)
        if (resumeFile != null && !resumeFile.isEmpty()) {
            try {
                candidate.setResumeData(resumeFile.getBytes());
                candidate.setResumeName(resumeFile.getOriginalFilename());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file");
            }
        }

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