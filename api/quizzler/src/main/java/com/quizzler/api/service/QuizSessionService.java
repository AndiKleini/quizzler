package com.quizzler.api.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import com.quizzler.api.domain.Question;
import com.quizzler.api.domain.QuizSession;
import com.quizzler.api.dto.QuizSessionDto;
import com.quizzler.api.repository.QuestionRepository;
import com.quizzler.api.repository.QuizSessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QuizSessionService {

    private static final long NO_QUESTION = 0L;

    private final QuizSessionRepository quizSessionRepository;
    private final QuestionRepository questionRepository;

    public QuizSessionService(QuizSessionRepository quizSessionRepository, QuestionRepository questionRepository) {
        this.quizSessionRepository = quizSessionRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional
    public QuizSessionDto createSession() {
        List<Question> questions = questionRepository.findAll();
        if (questions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No question available to assign to a session");
        }

        Question assigned = questions.get(ThreadLocalRandom.current().nextInt(questions.size()));
        QuizSession session = new QuizSession(
                UUID.randomUUID().toString(),
                assigned.getId(),
                NO_QUESTION,
                NO_QUESTION);
        return toDto(quizSessionRepository.save(session));
    }

    @Transactional(readOnly = true)
    public QuizSessionDto getSession(String publicId) {
        QuizSession session = quizSessionRepository.findByPublicId(publicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Session " + publicId + " not found"));
        return toDto(session);
    }

    private QuizSessionDto toDto(QuizSession session) {
        return new QuizSessionDto(
                session.getPublicId(),
                session.getCurrentQuestion(),
                session.getNextQuestion(),
                session.getPreviousQuestion());
    }
}
