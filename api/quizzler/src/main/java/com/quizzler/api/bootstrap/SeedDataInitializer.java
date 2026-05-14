package com.quizzler.api.bootstrap;

import java.util.List;

import com.quizzler.api.domain.SinglePickOption;
import com.quizzler.api.domain.SinglePickQuestion;
import com.quizzler.api.repository.QuestionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SeedDataInitializer implements CommandLineRunner {

    private final QuestionRepository questionRepository;

    public SeedDataInitializer(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (questionRepository.count() > 0) {
            return;
        }

        SinglePickQuestion question = new SinglePickQuestion(
                "Question ES 1",
                "Which of the following qualities can most likely be improved by using a layered architecture?");

        List.of(
                "Runtime efficiency (performance).",
                "Flexibility in modifying or changing the system.",
                "Flexibility at runtime (configurability).",
                "Non-repudiability."
        ).forEach(text -> question.addOption(new SinglePickOption(text)));

        SinglePickQuestion saved = questionRepository.save(question);
        saved.setCorrectOptionId(saved.getOptions().get(1).getId());
        questionRepository.save(saved);
    }
}
