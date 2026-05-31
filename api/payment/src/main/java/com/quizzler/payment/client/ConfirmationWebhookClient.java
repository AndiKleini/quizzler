package com.quizzler.payment.client;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

/**
 * Outbound adapter that notifies the merchant that a payment was confirmed. It POSTs to the
 * success-webhook URL the merchant supplied when the payment was created. The URL is treated as an
 * opaque, caller-provided callback, so the payment API stays decoupled from the merchant's endpoint
 * layout (see the "Integration of two REST APIs" cross-cutting concept).
 */
@Component
public class ConfirmationWebhookClient {

    private final RestTemplate restTemplate;

    public ConfirmationWebhookClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * @param webhookSuccessUrl the merchant's success-webhook URL, captured at payment creation
     */
    public void notifyConfirmation(String webhookSuccessUrl) {
        try {
            restTemplate.postForLocation(webhookSuccessUrl, null);
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Confirmation webhook " + webhookSuccessUrl + " failed", ex);
        }
    }
}
