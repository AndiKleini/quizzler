package com.quizzler.api.dto;

public class QuizAttemptDto {

    private final String attemptId;
    private final String sessionId;
    private final long questionId;
    private final boolean completed;

    public QuizAttemptDto(String attemptId, String sessionId, long questionId, boolean completed) {
        this.attemptId = attemptId;
        this.sessionId = sessionId;
        this.questionId = questionId;
        this.completed = completed;
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

    public boolean isCompleted() {
        return completed;
    }
}
