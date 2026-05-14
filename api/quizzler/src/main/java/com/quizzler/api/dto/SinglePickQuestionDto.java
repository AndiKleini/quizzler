package com.quizzler.api.dto;

import java.util.List;

public class SinglePickQuestionDto {

    private final Long id;
    private final String type = "SINGLE_PICK";
    private final String title;
    private final String text;
    private final List<SinglePickOptionDto> options;

    public SinglePickQuestionDto(Long id, String title, String text, List<SinglePickOptionDto> options) {
        this.id = id;
        this.title = title;
        this.text = text;
        this.options = options;
    }

    public Long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public List<SinglePickOptionDto> getOptions() {
        return options;
    }
}
