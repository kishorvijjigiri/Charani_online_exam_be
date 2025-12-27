package com.jobportel.boot.controller;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobportel.boot.dto.AnswerDTO;
import com.jobportel.boot.dto.SubmitRequest;
import com.jobportel.boot.model.Answer;
import com.jobportel.boot.model.Question;
import com.jobportel.boot.model.Result;
import com.jobportel.boot.repository.AnswerRepository;
import com.jobportel.boot.repository.QuestionRepository;
import com.jobportel.boot.repository.ResultRepository;

@RestController
@RequestMapping("/api/result")
@CrossOrigin(origins = "http://localhost:3000")
public class ResultController {

    @Autowired
    private QuestionRepository questionRepo;

    @Autowired
    private ResultRepository resultRepo;

    @Autowired
    private AnswerRepository answerRepo;
    
    @PostMapping("/submit")
    public Result submitExam(@RequestBody SubmitRequest request) {

        Result existing = resultRepo.findByCandidateEmail(request.getCandidateEmail()).orElse(null);
        if (existing != null) return existing;

        int apt = 0, rea = 0, com = 0;
        int totalExamQuestions = 60; 

        for (AnswerDTO dto : request.getAnswers()) {
            Question q = questionRepo.findById(dto.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            boolean correct = q.getCorrectOption().trim().equalsIgnoreCase(dto.getSelectedOption().trim());

            Answer ans = new Answer();
            ans.setCandidateEmail(request.getCandidateEmail());
            ans.setQuestionId(q.getId());
            ans.setSubmittedAnswer(dto.getSelectedOption());
            ans.setCorrectAnswer(q.getCorrectOption());
            ans.setCorrect(correct);
            answerRepo.save(ans);

            if (correct) {
                String section = q.getSection().trim().toUpperCase();
                switch (section) {
                    case "APTITUDE": apt++; break;
                    case "REASONING": rea++; break;
                    case "COMMUNICATION": com++; break; 
                }
            }
        }

        int totalCorrect = apt + rea + com;
        double percentage = ((double) totalCorrect / totalExamQuestions) * 100;

        Result result = new Result();
        result.setCandidateEmail(request.getCandidateEmail());
        result.setAptitudeCorrect(apt);
        result.setReasoningCorrect(rea);
        result.setCommunicationCorrect(com);
        result.setTotalCorrect(totalCorrect);
        result.setPercentage(Math.round(percentage * 100.0) / 100.0); 

        return resultRepo.save(result);
    }

    // FIXED: Added explicit name for PathVariable
    @GetMapping("/email/{email}")
    public Result getResultByEmail(@PathVariable(name = "email") String email) {
        return resultRepo.findByCandidateEmail(email)
            .orElseThrow(() -> new RuntimeException("Result not found"));
    }

    // FIXED: Added explicit names for RequestParams
    @GetMapping("/search")
    public List<Result> searchResults(
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "minPercentage", required = false) Double minPercentage) {
        
        String emailFilter = (email != null && !email.trim().isEmpty()) ? email : null;
        
        // Handle null percentage by passing 0 to the query
        Double pctFilter = (minPercentage != null) ? minPercentage : 0.0;
        
        return resultRepo.findResultsWithFilters(emailFilter, pctFilter);
    }
}