package com.jobportel.boot.controller;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobportel.boot.model.Question;
import com.jobportel.boot.repository.QuestionRepository;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ExamController {

    @Autowired
    private QuestionRepository questionRepo;

    /**
     * Fetch questions for a section that are "fixed" for a specific email.
     * Use @RequestParam to get the email from the frontend.
     */
    @GetMapping("/questions/{section}")
    public List<Question> getQuestions(
            @PathVariable("section") String section, 
            @RequestParam("email") String email) {
        
        String formattedSection = section.trim();
        
        // 1. Fetch the list of questions (e.g., top 20 or all for that section)
        // Note: It's better to fetch a consistent list first, then shuffle it.
        List<Question> questions = questionRepo.findBySectionIgnoreCase(formattedSection);
        
        // 2. Create a Random object using the email's hash as a SEED
        // This is the "magic" part: same email = same seed = same shuffle order
        long seed = email.toLowerCase().trim().hashCode();
        Collections.shuffle(questions, new Random(seed));
        
        // 3. Limit the list to 20 questions if needed
        int limit = Math.min(questions.size(), 20);
        List<Question> finalQuestions = questions.subList(0, limit);
        
        System.out.println("User: " + email + " | Section: " + formattedSection + " | Questions: " + finalQuestions.size());
        
        return finalQuestions;
    }
}