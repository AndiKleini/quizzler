package com.quizzler.api.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name = "single_pick_question")
@DiscriminatorValue("SINGLE_PICK")
public class SinglePickQuestion extends Question {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", nullable = false)
    @OrderBy("id ASC")
    private List<SinglePickOption> options = new ArrayList<>();

    @Column(name = "correct_option_id")
    private Long correctOptionId;

    protected SinglePickQuestion() {
        super();
    }

    public SinglePickQuestion(String title, String text) {
        super(title, text);
    }

    public List<SinglePickOption> getOptions() {
        return options;
    }

    public void addOption(SinglePickOption option) {
        this.options.add(option);
    }

    public Long getCorrectOptionId() {
        return correctOptionId;
    }

    public void setCorrectOptionId(Long correctOptionId) {
        this.correctOptionId = correctOptionId;
    }
}
