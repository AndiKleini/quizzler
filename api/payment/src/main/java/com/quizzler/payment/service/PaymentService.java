package com.quizzler.payment.service;

import java.time.Instant;
import java.util.UUID;

import com.quizzler.payment.domain.Payment;
import com.quizzler.payment.dto.PaymentDetailDto;
import com.quizzler.payment.dto.PaymentDto;
import com.quizzler.payment.dto.PaymentRequestDto;
import com.quizzler.payment.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public PaymentDetailDto getPayment(String paymentId) {
        Payment payment = paymentRepository.findByPublicId(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Payment " + paymentId + " not found"));
        return new PaymentDetailDto(payment.getPrice(), payment.getRedirectUrl());
    }

    @Transactional
    public PaymentDto createPayment(PaymentRequestDto request) {
        Payment payment = 
        new Payment(
            UUID.randomUUID().toString(), 
            request.getTransactionId(), 
            request.getPrice(), 
            request.getProductId(),
            Instant.now(),
            request.getRedirectUrl(),
            request.getWebhookSuccessUrl(),
            request.getWebhookCancelUrl());
        Payment saved = paymentRepository.save(payment);
        return new PaymentDto(
            saved.getPublicId(), 
            saved.getTransactionId(),
            saved.getPrice(), 
            saved.getCreatedAt());
    }
}
