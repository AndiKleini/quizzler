package com.quizzler.api.dto;

public class QuizAttemptPurchaseDto {

    private final String purchaseId;
    private final String sessionId;

    public QuizAttemptPurchaseDto(String purchaseId, String sessionId) {
        this.purchaseId = purchaseId;
        this.sessionId = sessionId;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
