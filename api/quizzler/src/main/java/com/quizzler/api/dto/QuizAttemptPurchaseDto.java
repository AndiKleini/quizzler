package com.quizzler.api.dto;

public class QuizAttemptPurchaseDto {

    private final String purchaseId;
    private final String sessionId;
    private final int price;

    public QuizAttemptPurchaseDto(String purchaseId, String sessionId, int price) {
        this.purchaseId = purchaseId;
        this.sessionId = sessionId;
        this.price = price;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public int getPrice() {
        return price;
    }
}
