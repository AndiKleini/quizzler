package com.quizzler.api.controller;

import com.quizzler.api.dto.SinglePickQuestionDto;
import com.quizzler.api.service.QuestionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/question")
public class QuestionController {

    private final QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/{questionId}")
    public SinglePickQuestionDto getQuestion(@PathVariable long questionId) {
        return questionService.getSinglePickQuestion(questionId);
    }
}
