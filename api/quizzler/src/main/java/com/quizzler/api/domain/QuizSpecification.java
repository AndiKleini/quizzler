package com.quizzler.api.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

@Entity
@Table(name = "quiz_specification")
public class QuizSpecification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    @CollectionTable(
            name = "quiz_specification_question",
            joinColumns = @JoinColumn(name = "quiz_specification_id", nullable = false))
    @OrderColumn(name = "position", nullable = false)
    @Column(name = "question_id", nullable = false)
    private List<Long> questionIds = new ArrayList<>();

    protected QuizSpecification() {
    }

    public QuizSpecification(List<Long> questionIds) {
        this.questionIds.addAll(questionIds);
    }

    public Long getId() {
        return id;
    }

    public List<Long> getQuestionIds() {
        return Collections.unmodifiableList(questionIds);
    }
}
