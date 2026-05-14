package com.quizzler.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.List;

import com.quizzler.api.domain.SinglePickOption;
import com.quizzler.api.domain.SinglePickQuestion;
import com.quizzler.api.dto.SinglePickOptionDto;
import com.quizzler.api.dto.SinglePickQuestionDto;
import com.quizzler.api.repository.QuestionRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    private static final String QUESTION_TITLE = "Layered architecture question";
    private static final String QUESTION_TEXT = "Which quality is best improved by a layered architecture?";
    private static final String OPTION_TEXT_1 = "Runtime efficiency.";
    private static final String OPTION_TEXT_2 = "Flexibility in modifying the system.";
    private static final String OPTION_TEXT_3 = "Runtime configurability.";
    private static final String OPTION_TEXT_4 = "Non-repudiability.";

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionService questionService;

    private SinglePickQuestion seededQuestion;

    @BeforeEach
    void setUp() {
        long[] optionIds = new long[]{10L, 20L, 30L, 40L};
        SinglePickQuestion question = new SinglePickQuestion(QUESTION_TITLE, QUESTION_TEXT);
        ReflectionTestUtils.setField(question, "id", 42L);
        for (int i = 0; i < optionIds.length; i++) {
            SinglePickOption option = new SinglePickOption(new String[]{OPTION_TEXT_1, OPTION_TEXT_2, OPTION_TEXT_3, OPTION_TEXT_4}[i]);
            ReflectionTestUtils.setField(option, "id", optionIds[i]);
            question.addOption(option);
        }
        seededQuestion = question;
        seededQuestion.setCorrectOptionId(20L);
    }

    @Test
    void getSinglePickQuestion_which_exists_is_returned() {
        when(questionRepository.findById(42L)).thenReturn(Optional.of(seededQuestion));

        SinglePickQuestionDto expectedDto = new SinglePickQuestionDto(
                42L,
                QUESTION_TITLE,
                QUESTION_TEXT,
                List.of(
                        new SinglePickOptionDto(10L, OPTION_TEXT_1),
                        new SinglePickOptionDto(20L, OPTION_TEXT_2),
                        new SinglePickOptionDto(30L, OPTION_TEXT_3),
                        new SinglePickOptionDto(40L, OPTION_TEXT_4)
                )
        );

        SinglePickQuestionDto dto = questionService.getSinglePickQuestion(42L);

        assertThat(dto).usingRecursiveComparison().isEqualTo(expectedDto);
    }

    @Test
    void getSinglePickQuestion_when_not_exists_throws() {
        when(questionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> questionService.getSinglePickQuestion(99L))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }
}
