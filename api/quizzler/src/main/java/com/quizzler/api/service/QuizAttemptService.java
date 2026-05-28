package com.quizzler.api.service;

import java.util.List;
import java.util.UUID;

import com.quizzler.api.domain.QuizAttempt;
import com.quizzler.api.domain.QuizSession;
import com.quizzler.api.dto.QuizAttemptDto;
import com.quizzler.api.repository.QuizAttemptRepository;
import com.quizzler.api.repository.QuizSessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QuizAttemptService {

    private final QuizSessionRepository quizSessionRepository;
    private final QuizAttemptRepository quizAttemptRepository;

    public QuizAttemptService(QuizSessionRepository quizSessionRepository,
                              QuizAttemptRepository quizAttemptRepository) {
        this.quizSessionRepository = quizSessionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
    }

    @Transactional
    public QuizAttemptDto createAttempt(String sessionPublicId) {
        QuizSession session = quizSessionRepository.findByPublicId(sessionPublicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Session " + sessionPublicId + " not found"));

        List<Long> questionIds = session.getSpecification().getQuestionIds();
        if (questionIds.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Quiz specification for session " + sessionPublicId + " has no questions");
        }

        QuizAttempt attempt = new QuizAttempt(
                UUID.randomUUID().toString(),
                session,
                questionIds.get(0));
        return toDto(quizAttemptRepository.save(attempt));
    }

    private QuizAttemptDto toDto(QuizAttempt attempt) {
        return new QuizAttemptDto(
                attempt.getPublicId(),
                attempt.getSession().getPublicId(),
                attempt.getQuestionId());
    }
}
