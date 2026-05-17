package com.quizzler.api.dto;

public class QuizSessionDto {

    private final String publicId;
    private final long currentQuestion;
    private final long nextQuestion;
    private final long previousQuestion;

    public QuizSessionDto(String publicId, long currentQuestion, long nextQuestion, long previousQuestion) {
        this.publicId = publicId;
        this.currentQuestion = currentQuestion;
        this.nextQuestion = nextQuestion;
        this.previousQuestion = previousQuestion;
    }

    public String getPublicId() {
        return publicId;
    }

    public long getCurrentQuestion() {
        return currentQuestion;
    }

    public long getNextQuestion() {
        return nextQuestion;
    }

    public long getPreviousQuestion() {
        return previousQuestion;
    }
}
