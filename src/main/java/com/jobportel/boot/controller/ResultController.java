package com.jobportel.boot.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

        Result existing = resultRepo
                .findByCandidateEmail(request.getCandidateEmail())
                .orElse(null);

        if (existing != null) {
            return existing;
        }

        int apt = 0, rea = 0, com = 0;
        int totalQuestions = request.getAnswers().size(); // Get total for percentage

        for (AnswerDTO dto : request.getAnswers()) {

            Question q = questionRepo.findById(dto.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            // Use trim() to remove hidden spaces and ignore case for comparison
            boolean correct = q.getCorrectOption().trim().equalsIgnoreCase(dto.getSelectedOption().trim());

            Answer ans = new Answer();
            ans.setCandidateEmail(request.getCandidateEmail());
            ans.setQuestionId(q.getId());
            ans.setSubmittedAnswer(dto.getSelectedOption());
            ans.setCorrectAnswer(q.getCorrectOption());
            ans.setCorrect(correct);

            answerRepo.save(ans);

            if (correct) {
                // Normalize the section name from DB to match the switch cases
                String sectionName = q.getSection().trim().toUpperCase();
                
                switch (sectionName) {
                    case "APTITUDE": 
                        apt++; 
                        break;
                    case "REASONING": 
                        rea++; 
                        break;
                    case "COMMUNICATION": 
                        com++; 
                        break;
                    default:
                        // This helps you see if there is a typo in your DB section names
                        System.out.println("No match found for section: [" + sectionName + "]");
                        break;
                }
            }
        }

        int totalCorrect = apt + rea + com;
        
        // Calculate percentage: (correct / total) * 100
        double percentage = (totalQuestions > 0) ? ((double) totalCorrect / totalQuestions) * 100 : 0.0;

        Result result = new Result();
        result.setCandidateEmail(request.getCandidateEmail());
        result.setAptitudeCorrect(apt);
        result.setReasoningCorrect(rea);
        result.setCommunicationCorrect(com);
        result.setTotalCorrect(totalCorrect);
        //result.setPercentage(percentage); // Set the calculated percentage

        return resultRepo.save(result);
    }
    @GetMapping("/email/{email}")
    public Result getResultByEmail(@PathVariable("email") String email) {
        return resultRepo.findByCandidateEmail(email)
            .orElseThrow(() -> new RuntimeException("Result not found"));
    }
}


