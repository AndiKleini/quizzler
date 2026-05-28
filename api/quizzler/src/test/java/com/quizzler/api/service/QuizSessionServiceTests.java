package com.quizzler.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.quizzler.api.domain.QuizSession;
import com.quizzler.api.domain.QuizSpecification;
import com.quizzler.api.dto.QuizSessionDto;
import com.quizzler.api.repository.QuizSessionRepository;
import com.quizzler.api.repository.QuizSpecificationRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class QuizSessionServiceTests {

    private static final String SESSION_PUBLIC_ID = "11111111-2222-3333-4444-555555555555";
    private static final long SEEDED_QUESTION_ID = 42L;

    @Mock
    private QuizSessionRepository quizSessionRepository;

    @Mock
    private QuizSpecificationRepository quizSpecificationRepository;

    @InjectMocks
    private QuizSessionService quizSessionService;

    @Test
    void createSession_persists_session_bound_to_first_specification() {
        QuizSpecification specification = new QuizSpecification(List.of(SEEDED_QUESTION_ID));
        when(quizSpecificationRepository.findAll()).thenReturn(List.of(specification));
        when(quizSessionRepository.save(any(QuizSession.class))).thenAnswer(call -> call.getArgument(0));

        QuizSessionDto dto = quizSessionService.createSession();

        assertThat(dto.getPublicId()).isNotBlank();
    }

    @Test
    void createSession_when_no_specification_exists_throws() {
        when(quizSpecificationRepository.findAll()).thenReturn(List.of());

        assertThatThrownBy(() -> quizSessionService.createSession())
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void getSession_which_exists_is_returned() {
        QuizSpecification specification = new QuizSpecification(List.of(SEEDED_QUESTION_ID));
        QuizSession session = new QuizSession(SESSION_PUBLIC_ID, specification);
        when(quizSessionRepository.findByPublicId(SESSION_PUBLIC_ID)).thenReturn(Optional.of(session));
        QuizSessionDto expected = new QuizSessionDto(SESSION_PUBLIC_ID);

        QuizSessionDto dto = quizSessionService.getSession(SESSION_PUBLIC_ID);

        assertThat(dto).usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void getSession_when_not_exists_throws() {
        when(quizSessionRepository.findByPublicId(SESSION_PUBLIC_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> quizSessionService.getSession(SESSION_PUBLIC_ID))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.NOT_FOUND));
    }
}
