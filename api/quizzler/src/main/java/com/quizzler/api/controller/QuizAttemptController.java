package com.quizzler.api.controller;

import com.quizzler.api.dto.QuizAttemptDto;
import com.quizzler.api.service.QuizAttemptService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session/{publicId}/attempt")
public class QuizAttemptController {

    private final QuizAttemptService quizAttemptService;

    public QuizAttemptController(QuizAttemptService quizAttemptService) {
        this.quizAttemptService = quizAttemptService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuizAttemptDto createAttempt(@PathVariable String publicId) {
        System.out.println("Creating attempt for session " + publicId);
        return quizAttemptService.createAttempt(publicId);
    }
}
