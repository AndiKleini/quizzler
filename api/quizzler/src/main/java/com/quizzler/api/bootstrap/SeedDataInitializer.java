package com.quizzler.api.bootstrap;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizzler.api.domain.QuizSession;
import com.quizzler.api.domain.QuizSpecification;
import com.quizzler.api.domain.SinglePickOption;
import com.quizzler.api.domain.SinglePickQuestion;
import com.quizzler.api.repository.QuestionRepository;
import com.quizzler.api.repository.QuizSessionRepository;
import com.quizzler.api.repository.QuizSpecificationRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SeedDataInitializer implements CommandLineRunner {

    static final String SEED_SESSION_PUBLIC_ID = "11111111-2222-3333-4444-555555555555";
    static final String WORKSHOP_SESSION_PUBLIC_ID = "session-part-1";
    static final String QUESTION_TITLE = "Question ES 1";
    static final String QUESTION_TEXT =
            "Which of the following qualities can most likely be improved by using a layered architecture?";
    static final String CORRECT_OPTION_TEXT = "Flexibility in modifying or changing the system.";
    static final List<String> OPTION_TEXTS = List.of(
            "Runtime efficiency (performance).",
            CORRECT_OPTION_TEXT,
            "Flexibility at runtime (configurability).",
            "Non-repudiability.");

    private final QuestionRepository questionRepository;
    private final QuizSpecificationRepository quizSpecificationRepository;
    private final QuizSessionRepository quizSessionRepository;
    private final ObjectMapper objectMapper;

    public SeedDataInitializer(QuestionRepository questionRepository,
                               QuizSpecificationRepository quizSpecificationRepository,
                               QuizSessionRepository quizSessionRepository,
                               ObjectMapper objectMapper) {
        this.questionRepository = questionRepository;
        this.quizSpecificationRepository = quizSpecificationRepository;
        this.quizSessionRepository = quizSessionRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Seed existing default session if it doesn't exist
        if (quizSessionRepository.findByPublicId(SEED_SESSION_PUBLIC_ID).isEmpty()) {
            long firstQuestionId = seedQuestion(OPTION_TEXTS);
            long secondQuestionId = seedQuestion(rotated(OPTION_TEXTS, 1));
            long thirdQuestionId = seedQuestion(rotated(OPTION_TEXTS, 2));

            QuizSpecification specification = quizSpecificationRepository.save(
                    new QuizSpecification(List.of(firstQuestionId, secondQuestionId, thirdQuestionId)));

            quizSessionRepository.save(new QuizSession(SEED_SESSION_PUBLIC_ID, specification));
        }

        // Seed workshop sessions
        seedWorkshopSession();
    }

    private void seedWorkshopSession() {
        try {
            ClassPathResource resource = new ClassPathResource("workshop-questions.json");
            InputStream inputStream = resource.getInputStream();
            List<SessionDto> sessionDtos = objectMapper.readValue(
                    inputStream, new TypeReference<List<SessionDto>>() {});

            for (SessionDto sessionDto : sessionDtos) {
                if (quizSessionRepository.findByPublicId(sessionDto.sessionId).isPresent()) {
                    continue;
                }

                // Create new questions
                List<Long> questionIds = new ArrayList<>();
                for (QuestionDto questionDto : sessionDto.questions) {
                    long questionId = seedQuestionFromDto(questionDto);
                    questionIds.add(questionId);
                }

                // Create new specification and session
                QuizSpecification specification = quizSpecificationRepository.save(
                        new QuizSpecification(questionIds));
                quizSessionRepository.save(new QuizSession(sessionDto.sessionId, specification));
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to load workshop questions", e);
        }
    }

    private long seedQuestionFromDto(QuestionDto dto) {
        SinglePickQuestion question = new SinglePickQuestion(dto.title, dto.text);
        dto.options.forEach(text -> question.addOption(new SinglePickOption(text)));
        SinglePickQuestion saved = questionRepository.save(question);

        long correctOptionId = saved.getOptions().stream()
                .filter(option -> dto.correctOption.equals(option.getText()))
                .findFirst()
                .map(SinglePickOption::getId)
                .orElseThrow(() -> new IllegalStateException(
                        "Question '" + dto.title + "' is missing the correct option text"));

        saved.setCorrectOptionId(correctOptionId);
        return questionRepository.save(saved).getId();
    }

    static class SessionDto {
        public String sessionId;
        public List<QuestionDto> questions;
    }

    static class QuestionDto {
        public String title;
        public String text;
        public List<String> options;
        public String correctOption;
    }

    private long seedQuestion(List<String> optionTexts) {
        SinglePickQuestion question = new SinglePickQuestion(QUESTION_TITLE, QUESTION_TEXT);
        optionTexts.forEach(text -> question.addOption(new SinglePickOption(text)));
        SinglePickQuestion saved = questionRepository.save(question);
        long correctOptionId = saved.getOptions().stream()
                .filter(option -> CORRECT_OPTION_TEXT.equals(option.getText()))
                .findFirst()
                .map(SinglePickOption::getId)
                .orElseThrow(() -> new IllegalStateException(
                        "Seeded question is missing the correct option text"));
        saved.setCorrectOptionId(correctOptionId);
        return questionRepository.save(saved).getId();
    }

    private static List<String> rotated(List<String> source, int shift) {
        List<String> rotated = new ArrayList<>(source);
        Collections.rotate(rotated, shift);
        return rotated;
    }
}
