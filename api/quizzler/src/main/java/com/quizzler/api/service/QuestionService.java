package com.quizzler.api.service;

import java.util.List;
import java.util.stream.Collectors;

import com.quizzler.api.domain.Question;
import com.quizzler.api.domain.SinglePickOption;
import com.quizzler.api.domain.SinglePickQuestion;
import com.quizzler.api.dto.SinglePickOptionDto;
import com.quizzler.api.dto.SinglePickQuestionDto;
import com.quizzler.api.repository.QuestionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Transactional(readOnly = true)
    public SinglePickQuestionDto getSinglePickQuestion(long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Question " + questionId + " not found"));

        if (!(question instanceof SinglePickQuestion)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Question " + questionId + " is not a single-pick question");
        }
        return toDto((SinglePickQuestion) question);
    }

    private SinglePickQuestionDto toDto(SinglePickQuestion question) {
        List<SinglePickOptionDto> options = question.getOptions().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return new SinglePickQuestionDto(
                question.getId(),
                question.getTitle(),
                question.getText(),
                options);
    }

    private SinglePickOptionDto toDto(SinglePickOption option) {
        return new SinglePickOptionDto(option.getId(), option.getText());
    }
}
