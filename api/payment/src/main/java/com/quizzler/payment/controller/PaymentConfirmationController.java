package com.quizzler.payment.controller;

import com.quizzler.payment.dto.PaymentConfirmationDto;
import com.quizzler.payment.service.PaymentConfirmationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment/{paymentId}/confirmation")
public class PaymentConfirmationController {

    private final PaymentConfirmationService paymentConfirmationService;

    public PaymentConfirmationController(PaymentConfirmationService paymentConfirmationService) {
        this.paymentConfirmationService = paymentConfirmationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentConfirmationDto confirmPayment(@PathVariable String paymentId) {
        return paymentConfirmationService.confirmPayment(paymentId);
    }
}
