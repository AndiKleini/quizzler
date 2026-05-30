package com.quizzler.payment.controller;

import com.quizzler.payment.dto.PaymentCancellationDto;
import com.quizzler.payment.service.PaymentCancellationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment/{paymentId}/cancellation")
public class PaymentCancellationController {

    private final PaymentCancellationService paymentCancellationService;

    public PaymentCancellationController(PaymentCancellationService paymentCancellationService) {
        this.paymentCancellationService = paymentCancellationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentCancellationDto cancelPayment(@PathVariable String paymentId) {
        return paymentCancellationService.cancelPayment(paymentId);
    }
}
