package com.quizzler.api.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.quizzler.api.domain.SinglePickOption;
import com.quizzler.api.domain.SinglePickQuestion;
import com.quizzler.api.dto.SinglePickOptionDto;
import com.quizzler.api.dto.SinglePickQuestionDto;
import com.quizzler.api.repository.QuestionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class QuestionControllerTests {

    private static final String QUESTION_TITLE = "Layered architecture question";
    private static final String QUESTION_TEXT = "Which quality is best improved by a layered architecture?";
    private static final String OPTION_TEXT_1 = "Runtime efficiency.";
    private static final String OPTION_TEXT_2 = "Flexibility in modifying the system.";

    @Autowired
    private QuestionRepository questionRepository;

    private static Long seededQuestionId = 0L;
    private Long firstOptionId;
    private Long secondOptionId;

    @BeforeEach
    void seedTestQuestion() {
        if (seededQuestionId != 0) {
            questionRepository.deleteById(seededQuestionId);
        }

        SinglePickQuestion question = new SinglePickQuestion(QUESTION_TITLE, QUESTION_TEXT);
        question.addOption(new SinglePickOption(OPTION_TEXT_1));
        question.addOption(new SinglePickOption(OPTION_TEXT_2));
        SinglePickQuestion saved = questionRepository.save(question);

        seededQuestionId = saved.getId();
        firstOptionId = saved.getOptions().get(0).getId();
        secondOptionId = saved.getOptions().get(1).getId();
    }

    @Test
    public void getSinglePickQuestion_when_exists_returns_expected_dto(@Autowired WebTestClient webTestClient) {
        SinglePickQuestionDto expected = new SinglePickQuestionDto(
                seededQuestionId,
                QUESTION_TITLE,
                QUESTION_TEXT,
                List.of(
                        new SinglePickOptionDto(firstOptionId, OPTION_TEXT_1),
                        new SinglePickOptionDto(secondOptionId, OPTION_TEXT_2)
                ));

        webTestClient.get().uri("/question/{id}", seededQuestionId).exchange()
                .expectStatus().isOk()
                .expectBody(SinglePickQuestionDto.class)
                .value(dto -> assertThat(dto).usingRecursiveComparison().isEqualTo(expected));
    }

    @Test
    public void getSinglePickQuestion_when_not_exists_returns_404(@Autowired WebTestClient webTestClient) {
        long missingId = seededQuestionId + 9999L;

        webTestClient.get().uri("/question/{id}", missingId).exchange()
                .expectStatus().isNotFound();
    }
}