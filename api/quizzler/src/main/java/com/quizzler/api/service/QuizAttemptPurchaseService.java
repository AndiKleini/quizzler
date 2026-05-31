package com.quizzler.api.service;

import java.util.UUID;

import com.quizzler.api.client.PaymentApiClient;
import com.quizzler.api.domain.QuizAttemptPurchase;
import com.quizzler.api.domain.QuizSession;
import com.quizzler.api.dto.PaymentInitiationDto;
import com.quizzler.api.dto.QuizAttemptPurchaseDto;
import com.quizzler.api.repository.QuizAttemptPurchaseRepository;
import com.quizzler.api.repository.QuizSessionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QuizAttemptPurchaseService {

    private static final int PRICE = 200;

    private final QuizSessionRepository quizSessionRepository;
    private final QuizAttemptPurchaseRepository quizAttemptPurchaseRepository;
    private final PaymentApiClient paymentApiClient;
    private final String apiBaseUrl;
    private final String uiBaseUrl;

    public QuizAttemptPurchaseService(QuizSessionRepository quizSessionRepository,
                                      QuizAttemptPurchaseRepository quizAttemptPurchaseRepository,
                                      PaymentApiClient paymentApiClient,
                                      @Value("${quizzler.api.base-url}") String apiBaseUrl,
                                      @Value("${quizzler.ui.base-url}") String uiBaseUrl) {
        this.quizSessionRepository = quizSessionRepository;
        this.quizAttemptPurchaseRepository = quizAttemptPurchaseRepository;
        this.paymentApiClient = paymentApiClient;
        this.apiBaseUrl = apiBaseUrl;
        this.uiBaseUrl = uiBaseUrl;
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

    @Transactional(readOnly = true)
    public PaymentInitiationDto initiatePayment(String sessionPublicId, String purchaseId) {
        QuizAttemptPurchase purchase = quizAttemptPurchaseRepository.findByPublicId(purchaseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Purchase " + purchaseId + " not found"));
        if (!purchase.getSession().getPublicId().equals(sessionPublicId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Purchase " + purchaseId + " does not belong to session " + sessionPublicId);
        }

        String redirectUrl = apiBaseUrl + "/session/" + sessionPublicId
                + "/quiz-attempt-purchase/" + purchase.getPublicId() + "/pymentconfirmation";
        String webhookSuccessUrl = uiBaseUrl + "/quiz-session/" + sessionPublicId
                + "/quiz-attempt-purchase-confirmed/";
        String webhookCancelUrl = uiBaseUrl + "/quiz-session/" + sessionPublicId
                + "/quiz-attempt-purchase-failed/";

        String paymentId = paymentApiClient.createPayment(
                purchase.getPublicId(), PRICE, redirectUrl, webhookSuccessUrl, webhookCancelUrl);
        return new PaymentInitiationDto(paymentId);
    }
}
