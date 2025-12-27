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
        @RequestBody Candidate candidate, 
        @RequestParam(value = "resume", required = false) MultipartFile resumeFile
    ) {
        // 1. Validate if candidate object exists
        if (candidate == null) {
            return ResponseEntity.badRequest().body("No candidate data received.");
        }

        // 2. Validate Email safely (Check null first, THEN trim)
        String rawEmail = candidate.getEmail();
        if (rawEmail == null || rawEmail.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }
        String email = rawEmail.trim();

        // 3. Database check for existing Email
        if (candidateRepository.findByEmail(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists!");
        }

        // 4. Validate Phone safely
        String rawPhone = candidate.getPhone();
        if (rawPhone == null || rawPhone.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Mobile Number is required");
        }
        String phone = rawPhone.trim();

        if (candidateRepository.findByPhone(phone).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Mobile Number already exists!");
        }

        // 5. Handle Resume Upload
        if (resumeFile != null && !resumeFile.isEmpty()) {
            try {
                candidate.setResumeData(resumeFile.getBytes());
                candidate.setResumeName(resumeFile.getOriginalFilename());
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing file");
            }
        }

        // 6. Set the cleaned/trimmed data back to the object
        candidate.setEmail(email);
        candidate.setPhone(phone);

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