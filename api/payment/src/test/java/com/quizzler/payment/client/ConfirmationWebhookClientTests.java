package com.quizzler.payment.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

class ConfirmationWebhookClientTests {

    private static final String WEBHOOK_URL =
            "http://merchant.test/session/s/quiz-attempt-purchase/p/confirmation";

    private MockRestServiceServer server;
    private ConfirmationWebhookClient confirmationWebhookClient;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        confirmationWebhookClient = new ConfirmationWebhookClient(restTemplate);
    }

    @Test
    void notifyConfirmation_posts_to_the_supplied_webhook_url() {
        server.expect(requestTo(WEBHOOK_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED));

        confirmationWebhookClient.notifyConfirmation(WEBHOOK_URL);

        server.verify();
    }

    @Test
    void notifyConfirmation_when_webhook_fails_throws_bad_gateway() {
        server.expect(requestTo(WEBHOOK_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        assertThatThrownBy(() -> confirmationWebhookClient.notifyConfirmation(WEBHOOK_URL))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        ex -> assertThat(ex.getStatus()).isEqualTo(HttpStatus.BAD_GATEWAY));
        server.verify();
    }
}
