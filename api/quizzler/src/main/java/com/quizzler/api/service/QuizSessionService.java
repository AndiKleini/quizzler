package com.quizzler.api.service;

import java.util.UUID;

import com.quizzler.api.domain.QuizSession;
import com.quizzler.api.dto.QuizSessionDto;
import com.quizzler.api.repository.QuizSessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QuizSessionService {

    private final QuizSessionRepository quizSessionRepository;

    public QuizSessionService(QuizSessionRepository quizSessionRepository) {
        this.quizSessionRepository = quizSessionRepository;
    }

    @Transactional
    public QuizSessionDto createSession() {
        QuizSession session = new QuizSession(UUID.randomUUID().toString());
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
        return new QuizSessionDto(session.getPublicId());
    }
}
