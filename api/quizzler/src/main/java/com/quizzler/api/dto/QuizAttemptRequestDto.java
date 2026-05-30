package com.quizzler.api.dto;

public class QuizAttemptRequestDto {

    private String purchaseId;

    public QuizAttemptRequestDto() {
    }

    public QuizAttemptRequestDto(String purchaseId) {
        this.purchaseId = purchaseId;
    }

    public String getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }
}
