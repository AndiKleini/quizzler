package com.quizzler.api.service;

import java.time.Instant;
import java.util.UUID;

import com.quizzler.api.client.PaymentApiClient;
import com.quizzler.api.domain.QuizAttemptPurchase;
import com.quizzler.api.domain.QuizAttemptPurchaseConfirmation;
import com.quizzler.api.domain.QuizSession;
import com.quizzler.api.dto.PaymentInitiationDto;
import com.quizzler.api.dto.QuizAttemptPurchaseConfirmationDto;
import com.quizzler.api.dto.QuizAttemptPurchaseDto;
import com.quizzler.api.repository.QuizAttemptPurchaseConfirmationRepository;
import com.quizzler.api.repository.QuizAttemptPurchaseRepository;
import com.quizzler.api.repository.QuizSessionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class QuizAttemptPurchaseService {

    private static final int PRICE = 200;

    private final QuizSessionRepository quizSessionRepository;
    private final QuizAttemptPurchaseRepository quizAttemptPurchaseRepository;
    private final QuizAttemptPurchaseConfirmationRepository quizAttemptPurchaseConfirmationRepository;
    private final PaymentApiClient paymentApiClient;
    private final String apiBaseUrl;
    private final String uiBaseUrl;

    public QuizAttemptPurchaseService(QuizSessionRepository quizSessionRepository,
                                      QuizAttemptPurchaseRepository quizAttemptPurchaseRepository,
                                      QuizAttemptPurchaseConfirmationRepository quizAttemptPurchaseConfirmationRepository,
                                      PaymentApiClient paymentApiClient,
                                      @Value("${quizzler.api.base-url}") String apiBaseUrl,
                                      @Value("${quizzler.ui.base-url}") String uiBaseUrl) {
                                        System.out.println("APIBaseUrl this is new " + apiBaseUrl);
        this.quizSessionRepository = quizSessionRepository;
        this.quizAttemptPurchaseRepository = quizAttemptPurchaseRepository;
        this.quizAttemptPurchaseConfirmationRepository = quizAttemptPurchaseConfirmationRepository;
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
        System.out.println("In initiate payment");
        QuizAttemptPurchase purchase = quizAttemptPurchaseRepository.findByPublicId(purchaseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Purchase " + purchaseId + " not found"));
        if (!purchase.getSession().getPublicId().equals(sessionPublicId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Purchase " + purchaseId + " does not belong to session " + sessionPublicId);
        }

        String redirectUrl = uiBaseUrl + "/quiz-session/" + sessionPublicId
                + "/quiz-attempt-purchase-confirmed/?purchaseId=" + purchase.getPublicId();
        String webhookSuccessUrl = apiBaseUrl + "/session/" + sessionPublicId
                + "/quiz-attempt-purchase/" + purchase.getPublicId() + "/confirmation";
        String webhookCancelUrl = uiBaseUrl + "/quiz-session/" + sessionPublicId
                + "/quiz-attempt-purchase-failed/";

        System.out.println("SuccessUrl specifiedf "  + webhookSuccessUrl);

        String paymentId = paymentApiClient.createPayment(
                purchase.getPublicId(), PRICE, purchase.getSession().getPublicId(), redirectUrl, webhookSuccessUrl, webhookCancelUrl);
        return new PaymentInitiationDto(paymentId);
    }

    @Transactional
    public QuizAttemptPurchaseConfirmationDto confirmPurchase(String sessionPublicId, String purchaseId) {

        // introduce an artificial delay in order to simulate that the api takes longer to response
        try {
                Thread.sleep(5000);
        } catch (InterruptedException ex) {
                throw new ResponseStatusException(
                        HttpStatus.INTERNAL_SERVER_ERROR, 
                        "Waiting thread was interrupted");
        }

        QuizAttemptPurchase purchase = quizAttemptPurchaseRepository.findByPublicId(purchaseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Purchase " + purchaseId + " not found"));
        if (!purchase.getSession().getPublicId().equals(sessionPublicId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Purchase " + purchaseId + " does not belong to session " + sessionPublicId);
        }

        // A duplicate confirmation is rejected by the unique quiz_attempt_purchase_id constraint at
        // insert time, so we let the database enforce it rather than issuing an extra existence query.
        try {
            QuizAttemptPurchaseConfirmation saved = quizAttemptPurchaseConfirmationRepository.saveAndFlush(
                    new QuizAttemptPurchaseConfirmation(UUID.randomUUID().toString(), purchase, Instant.now()));
            return new QuizAttemptPurchaseConfirmationDto(
                    saved.getPublicId(), purchase.getPublicId(), saved.getCreatedAt());
        } catch (DataIntegrityViolationException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Purchase " + purchaseId + " is already confirmed", ex);
        }
    }

    @Transactional(readOnly = true)
    public QuizAttemptPurchaseConfirmationDto getConfirmation(String sessionPublicId, String purchaseId) {
        QuizAttemptPurchase purchase = quizAttemptPurchaseRepository.findByPublicId(purchaseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Purchase " + purchaseId + " not found"));
        if (!purchase.getSession().getPublicId().equals(sessionPublicId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Purchase " + purchaseId + " does not belong to session " + sessionPublicId);
        }

        QuizAttemptPurchaseConfirmation confirmation = quizAttemptPurchaseConfirmationRepository
                .findByPurchasePublicId(purchaseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Purchase " + purchaseId + " is not confirmed yet"));
        return new QuizAttemptPurchaseConfirmationDto(
                confirmation.getPublicId(), purchase.getPublicId(), confirmation.getCreatedAt());
    }
}
