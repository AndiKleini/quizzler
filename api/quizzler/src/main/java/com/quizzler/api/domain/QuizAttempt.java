package com.quizzler.api.domain;

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
@Table(name = "quiz_attempt")
public class QuizAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "public_id", nullable = false, unique = true)
    private String publicId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_session_id", nullable = false)
    private QuizSession session;

    @Column(name = "question_id", nullable = false)
    private long questionId;

    protected QuizAttempt() {
    }

    public QuizAttempt(String publicId, QuizSession session, long questionId) {
        this.publicId = publicId;
        this.session = session;
        this.questionId = questionId;
    }

    public Long getId() {
        return id;
    }

    public String getPublicId() {
        return publicId;
    }

    public QuizSession getSession() {
        return session;
    }

    public long getQuestionId() {
        return questionId;
    }
}
