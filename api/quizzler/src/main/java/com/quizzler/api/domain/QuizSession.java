package com.quizzler.api.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "quiz_session")
public class QuizSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private String publicId;

    @Column(name = "current_question", nullable = false)
    private long currentQuestion;

    @Column(name = "next_question", nullable = false)
    private long nextQuestion;

    @Column(name = "previous_question", nullable = false)
    private long previousQuestion;

    protected QuizSession() {
    }

    public QuizSession(String publicId, long currentQuestion, long nextQuestion, long previousQuestion) {
        this.publicId = publicId;
        this.currentQuestion = currentQuestion;
        this.nextQuestion = nextQuestion;
        this.previousQuestion = previousQuestion;
    }

    public Long getId() {
        return id;
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
