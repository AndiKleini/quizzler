package com.quizzler.api.dto;

public class SinglePickOptionDto {

    private final Long id;
    private final String text;

    public SinglePickOptionDto(Long id, String text) {
        this.id = id;
        this.text = text;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }
}
