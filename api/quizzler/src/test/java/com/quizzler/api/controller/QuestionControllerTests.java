package com.quizzler.api.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class QuestionControllerTests {

    @Test
    public void contextLoads(@Autowired WebTestClient webTestClient) {
        ResponseSpec response = webTestClient.get().uri("/question/1").exchange();
        response.expectStatus().isOk();
    }
}
