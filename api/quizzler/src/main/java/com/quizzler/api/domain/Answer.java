package com.quizzler.api.domain;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "answer")
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_attempt_id", nullable = false)
    private QuizAttempt attempt;

    @Column(name = "question_id", nullable = false)
    private long questionId;

    @Column(name = "selected_option_id", nullable = false)
    private long selectedOptionId;

    @Column(name = "submitted_at", nullable = false)
    private Instant submittedAt;

    protected Answer() {
    }

    public Answer(QuizAttempt attempt, long questionId, long selectedOptionId, Instant submittedAt) {
        this.attempt = attempt;
        this.questionId = questionId;
        this.selectedOptionId = selectedOptionId;
        this.submittedAt = submittedAt;
    }

    public Long getId() {
        return id;
    }

    public QuizAttempt getAttempt() {
        return attempt;
    }

    public long getQuestionId() {
        return questionId;
    }

    public long getSelectedOptionId() {
        return selectedOptionId;
    }

    public Instant getSubmittedAt() {
        return submittedAt;
    }
}
