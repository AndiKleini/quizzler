package com.quizzler.api.service;

import java.time.Instant;

import com.quizzler.api.domain.Answer;
import com.quizzler.api.domain.QuizAttempt;
import com.quizzler.api.dto.AnswerDto;
import com.quizzler.api.dto.AnswerSubmissionDto;
import com.quizzler.api.repository.AnswerRepository;
import com.quizzler.api.repository.QuizAttemptRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AnswerService {

    private final QuizAttemptRepository quizAttemptRepository;
    private final AnswerRepository answerRepository;

    public AnswerService(QuizAttemptRepository quizAttemptRepository,
                         AnswerRepository answerRepository) {
        this.quizAttemptRepository = quizAttemptRepository;
        this.answerRepository = answerRepository;
    }

    public AnswerDto submitAnswer(String attemptPublicId, AnswerSubmissionDto submission) {
        QuizAttempt attempt = quizAttemptRepository.findByPublicId(attemptPublicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Attempt " + attemptPublicId + " not found"));

        Answer answer = new Answer(
                attempt,
                submission.getQuestionId(),
                submission.getSelectedOptionId(),
                Instant.now());
        return toDto(answerRepository.save(answer));
    }

    private AnswerDto toDto(Answer answer) {
        return new AnswerDto(
                answer.getId(),
                answer.getAttempt().getPublicId(),
                answer.getQuestionId(),
                answer.getSelectedOptionId(),
                answer.getSubmittedAt());
    }
}
