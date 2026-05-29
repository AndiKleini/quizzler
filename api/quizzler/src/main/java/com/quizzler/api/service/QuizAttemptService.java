package com.quizzler.api.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.quizzler.api.domain.Answer;
import com.quizzler.api.domain.QuizAttempt;
import com.quizzler.api.domain.QuizSession;
import com.quizzler.api.dto.QuizAttemptDto;
import com.quizzler.api.repository.AnswerRepository;
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
    private final AnswerRepository answerRepository;

    public QuizAttemptService(QuizSessionRepository quizSessionRepository,
                              QuizAttemptRepository quizAttemptRepository,
                              AnswerRepository answerRepository) {
        this.quizSessionRepository = quizSessionRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.answerRepository = answerRepository;
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

    @Transactional(readOnly = true)
    public QuizAttemptDto getAttempt(String sessionPublicId, String attemptPublicId) {
        QuizAttempt attempt = quizAttemptRepository.findByPublicId(attemptPublicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Attempt " + attemptPublicId + " not found"));

        if (!attempt.getSession().getPublicId().equals(sessionPublicId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Attempt " + attemptPublicId + " does not belong to session " + sessionPublicId);
        }

        Set<Long> answeredQuestionIds = answerRepository.findByAttempt(attempt).stream()
                .map(Answer::getQuestionId)
                .collect(Collectors.toSet());
        Optional<Long> nextQuestionId = attempt.getSession().getSpecification().getQuestionIds().stream()
                .sorted((id1, id2) -> Long.compare(id1, id2))
                .filter(id -> !answeredQuestionIds.contains(id))
                .findFirst();

        return new QuizAttemptDto(
                attempt.getPublicId(),
                sessionPublicId,
                nextQuestionId.orElse(0L),
                nextQuestionId.isEmpty());
    }

    private QuizAttemptDto toDto(QuizAttempt attempt) {
        return new QuizAttemptDto(
                attempt.getPublicId(),
                attempt.getSession().getPublicId(),
                attempt.getQuestionId(),
                false);
    }
}
