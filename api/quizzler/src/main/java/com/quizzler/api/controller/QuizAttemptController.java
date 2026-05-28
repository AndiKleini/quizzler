package com.quizzler.api.controller;

import com.quizzler.api.dto.AnswerDto;
import com.quizzler.api.dto.AnswerSubmissionDto;
import com.quizzler.api.dto.QuizAttemptDto;
import com.quizzler.api.service.AnswerService;
import com.quizzler.api.service.QuizAttemptService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session/{publicId}/attempt")
public class QuizAttemptController {

    private final QuizAttemptService quizAttemptService;
    private final AnswerService answerService;

    public QuizAttemptController(QuizAttemptService quizAttemptService, AnswerService answerService) {
        this.quizAttemptService = quizAttemptService;
        this.answerService = answerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuizAttemptDto createAttempt(@PathVariable String publicId) {
        return quizAttemptService.createAttempt(publicId);
    }

    @PostMapping("/{attemptPublicId}/answer")
    @ResponseStatus(HttpStatus.CREATED)
    public AnswerDto submitAnswer(@PathVariable String publicId,
                                  @PathVariable String attemptPublicId,
                                  @RequestBody AnswerSubmissionDto submission) {
        return answerService.submitAnswer(attemptPublicId, submission);
    }
}
