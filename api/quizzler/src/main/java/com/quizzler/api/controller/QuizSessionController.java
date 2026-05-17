package com.quizzler.api.controller;

import com.quizzler.api.dto.QuizSessionDto;
import com.quizzler.api.service.QuizSessionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session")
public class QuizSessionController {

    private final QuizSessionService quizSessionService;

    public QuizSessionController(QuizSessionService quizSessionService) {
        this.quizSessionService = quizSessionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuizSessionDto createSession() {
        return quizSessionService.createSession();
    }

    @GetMapping("/{publicId}")
    public QuizSessionDto getSession(@PathVariable String publicId) {
        return quizSessionService.getSession(publicId);
    }
}
