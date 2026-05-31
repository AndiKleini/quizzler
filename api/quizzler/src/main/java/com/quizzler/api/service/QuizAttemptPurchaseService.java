package com.quizzler.api.service;

import java.util.UUID;

import com.quizzler.api.domain.QuizAttemptPurchase;
import com.quizzler.api.domain.QuizSession;
import com.quizzler.api.dto.QuizAttemptPurchaseDto;
import com.quizzler.api.repository.QuizAttemptPurchaseRepository;
import com.quizzler.api.repository.QuizSessionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QuizAttemptPurchaseService {

    private static final int PRICE = 200;

    private final QuizSessionRepository quizSessionRepository;
    private final QuizAttemptPurchaseRepository quizAttemptPurchaseRepository;

    public QuizAttemptPurchaseService(QuizSessionRepository quizSessionRepository,
                                      QuizAttemptPurchaseRepository quizAttemptPurchaseRepository) {
        this.quizSessionRepository = quizSessionRepository;
        this.quizAttemptPurchaseRepository = quizAttemptPurchaseRepository;
    }

    @Transactional
    public QuizAttemptPurchaseDto createPurchase(String sessionPublicId) {
        QuizSession session = quizSessionRepository.findByPublicId(sessionPublicId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Session " + sessionPublicId + " not found"));

        QuizAttemptPurchase purchase = new QuizAttemptPurchase(UUID.randomUUID().toString(), session);
        QuizAttemptPurchase saved = quizAttemptPurchaseRepository.save(purchase);
        return new QuizAttemptPurchaseDto(saved.getPublicId(), session.getPublicId(), PRICE);
    }
}
