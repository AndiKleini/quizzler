package com.quizzler.api.dto;

import java.time.Instant;

public class AnswerDto {

    private final Long id;
    private final String attemptId;
    private final long questionId;
    private final long selectedOptionId;
    private final long correctOptionId;
    private final Instant submittedAt;

    public AnswerDto(Long id,
                     String attemptId,
                     long questionId,
                     long selectedOptionId,
                     long correctOptionId,
                     Instant submittedAt) {
        this.id = id;
        this.attemptId = attemptId;
        this.questionId = questionId;
        this.selectedOptionId = selectedOptionId;
        this.correctOptionId = correctOptionId;
        this.submittedAt = submittedAt;
    }

    public Long getId() {
        return id;
    }

    public String getAttemptId() {
        return attemptId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public long getSelectedOptionId() {
        return selectedOptionId;
    }

    public long getCorrectOptionId() {
        return correctOptionId;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }
}
