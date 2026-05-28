package com.quizzler.api.service;

import java.util.UUID;

import com.quizzler.api.domain.QuizSession;
import com.quizzler.api.domain.QuizSpecification;
import com.quizzler.api.dto.QuizSessionDto;
import com.quizzler.api.repository.QuizSessionRepository;
import com.quizzler.api.repository.QuizSpecificationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QuizSessionService {

    private final QuizSessionRepository quizSessionRepository;
    private final QuizSpecificationRepository quizSpecificationRepository;

    public QuizSessionService(QuizSessionRepository quizSessionRepository,
                              QuizSpecificationRepository quizSpecificationRepository) {
        this.quizSessionRepository = quizSessionRepository;
        this.quizSpecificationRepository = quizSpecificationRepository;
    }

    @Transactional
    public QuizSessionDto createSession() {
        QuizSpecification specification = quizSpecificationRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT,
                        "No quiz specification available to assign to a session"));
        QuizSession session = new QuizSession(UUID.randomUUID().toString(), specification);
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
