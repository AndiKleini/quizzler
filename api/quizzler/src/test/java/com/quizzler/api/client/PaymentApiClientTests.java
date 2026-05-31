package com.quizzler.api.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.jsonPath;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

class PaymentApiClientTests {

    private static final String BASE_URL = "http://payment.test";
    private static final String TRANSACTION_ID = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee";
    private static final int PRICE = 200;
    private static final String PAYMENT_ID = "11111111-2222-3333-4444-555555555555";
    private static final String REDIRECT_URL = "http://localhost:8080/session/s/quiz-attempt-purchase/p/pymentconfirmation";
    private static final String WEBHOOK_SUCCESS_URL = "http://localhost:4200/quiz-session/s/quiz-attempt-purchase-confirmed/";
    private static final String WEBHOOK_CANCEL_URL = "http://localhost:4200/quiz-session/s/quiz-attempt-purchase-failed/";

    private MockRestServiceServer server;
    private PaymentApiClient paymentApiClient;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        paymentApiClient = new PaymentApiClient(restTemplate, BASE_URL);
    }

    @Test
    void createPayment_posts_transaction_price_and_urls_and_returns_payment_id() {
        server.expect(requestTo(BASE_URL + "/payment"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(jsonPath("$.transactionId").value(TRANSACTION_ID))
                .andExpect(jsonPath("$.price").value(PRICE))
                .andExpect(jsonPath("$.redirectUrl").value(REDIRECT_URL))
                .andExpect(jsonPath("$.webhookSuccessUrl").value(WEBHOOK_SUCCESS_URL))
                .andExpect(jsonPath("$.webhookCancelUrl").value(WEBHOOK_CANCEL_URL))
                .andRespond(withSuccess(
                        "{\"paymentId\":\"" + PAYMENT_ID + "\",\"transactionId\":\"" + TRANSACTION_ID
                                + "\",\"price\":" + PRICE + ",\"createdAt\":\"2026-05-31T00:00:00Z\"}",
                        MediaType.APPLICATION_JSON));

        String paymentId = paymentApiClient.createPayment(
                TRANSACTION_ID, PRICE, REDIRECT_URL, WEBHOOK_SUCCESS_URL, WEBHOOK_CANCEL_URL);

        assertThat(paymentId).isEqualTo(PAYMENT_ID);
        server.verify();
    }
}
