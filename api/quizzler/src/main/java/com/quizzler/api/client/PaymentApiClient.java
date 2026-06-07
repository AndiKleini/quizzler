package com.quizzler.api.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

/**
 * Outbound adapter to the payment API. Creates a payment for a quiz-attempt purchase and
 * returns the generated payment id.
 */
@Component
public class PaymentApiClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PaymentApiClient(RestTemplate restTemplate,
                            @Value("${payment.api.base-url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    /**
     * @param transactionId     identifier of the purchase this payment settles
     * @param price             amount in cents
     * @param productId         identifier of the product being purchased   
     * @param redirectUrl       where the payment UI sends the user back to after payment
     * @param webhookSuccessUrl webhook the payment provider calls once the payment is settled
     * @param webhookCancelUrl  webhook the payment provider calls once the payment is cancelled
     * @return the payment id assigned by the payment API
     */
    public String createPayment(String transactionId,
                                int price,
                                String productId,
                                String redirectUrl,
                                String webhookSuccessUrl,
                                String webhookCancelUrl) {
        PaymentCreationRequest request = new PaymentCreationRequest(
                transactionId, price, productId, redirectUrl, webhookSuccessUrl, webhookCancelUrl);
        PaymentCreationResponse response = restTemplate.postForObject(
                baseUrl + "/payment", request, PaymentCreationResponse.class);
        if (response == null || response.getPaymentId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Payment API did not return a payment id");
        }
        return response.getPaymentId();
    }
}
