package com.quizzler.api.dto;

public class QuizAttemptDto {

    private final String attemptId;
    private final String sessionId;
    private final long questionId;

    public QuizAttemptDto(String attemptId, String sessionId, long questionId) {
        this.attemptId = attemptId;
        this.sessionId = sessionId;
        this.questionId = questionId;
    }

    public String getAttemptId() {
        return attemptId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public long getQuestionId() {
        return questionId;
    }
}
