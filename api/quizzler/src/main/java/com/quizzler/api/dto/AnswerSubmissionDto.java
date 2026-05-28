package com.quizzler.api.dto;

public class AnswerSubmissionDto {

    private long questionId;
    private long selectedOptionId;

    public AnswerSubmissionDto() {
    }

    public AnswerSubmissionDto(long questionId, long selectedOptionId) {
        this.questionId = questionId;
        this.selectedOptionId = selectedOptionId;
    }

    public long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(long questionId) {
        this.questionId = questionId;
    }

    public long getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(long selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }
}
