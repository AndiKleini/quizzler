package com.quizzler.api.dto;

import java.time.Instant;

public class QuizAttemptPurchaseConfirmationDto {

    private final String confirmationId;
    private final String purchaseId;
    private final Instant createdAt;

    public QuizAttemptPurchaseConfirmationDto(String confirmationId, String purchaseId, Instant createdAt) {
        this.confirmationId = confirmationId;
        this.purchaseId = purchaseId;
        this.createdAt = createdAt;
    }

    public String getConfirmationId() {
        return confirmationId;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
