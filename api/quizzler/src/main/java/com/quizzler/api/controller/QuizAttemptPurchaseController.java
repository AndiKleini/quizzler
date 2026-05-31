package com.quizzler.api.controller;

import com.quizzler.api.dto.PaymentInitiationDto;
import com.quizzler.api.dto.QuizAttemptPurchaseConfirmationDto;
import com.quizzler.api.dto.QuizAttemptPurchaseDto;
import com.quizzler.api.service.QuizAttemptPurchaseService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session/{sessionId}/quiz-attempt-purchase")
public class QuizAttemptPurchaseController {

    private final QuizAttemptPurchaseService quizAttemptPurchaseService;

    public QuizAttemptPurchaseController(QuizAttemptPurchaseService quizAttemptPurchaseService) {
        this.quizAttemptPurchaseService = quizAttemptPurchaseService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public QuizAttemptPurchaseDto createPurchase(@PathVariable String sessionId) {
        return quizAttemptPurchaseService.createPurchase(sessionId);
    }

    @PostMapping("/{purchaseId}/payment")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentInitiationDto initiatePayment(@PathVariable String sessionId,
                                                @PathVariable String purchaseId) {
        return quizAttemptPurchaseService.initiatePayment(sessionId, purchaseId);
    }

    @PostMapping("/{purchaseId}/confirmation")
    @ResponseStatus(HttpStatus.CREATED)
    public QuizAttemptPurchaseConfirmationDto confirmPurchase(@PathVariable String sessionId,
                                                              @PathVariable String purchaseId) {
        return quizAttemptPurchaseService.confirmPurchase(sessionId, purchaseId);
    }

    @GetMapping("/{purchaseId}/confirmation")
    public QuizAttemptPurchaseConfirmationDto getConfirmation(@PathVariable String sessionId,
                                                              @PathVariable String purchaseId) {
        return quizAttemptPurchaseService.getConfirmation(sessionId, purchaseId);
    }
}
