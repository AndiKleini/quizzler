package com.quizzler.payment.repository;

import com.quizzler.payment.domain.Payment;
import com.quizzler.payment.domain.PaymentConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentConfirmationRepository extends JpaRepository<PaymentConfirmation, Long> {

    boolean existsByPayment(Payment payment);
}
