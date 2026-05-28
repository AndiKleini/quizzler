package com.quizzler.api.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public class QuizSessionDto {

    private final String publicId;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public QuizSessionDto(String publicId) {
        this.publicId = publicId;
    }

    public String getPublicId() {
        return publicId;
    }
}
