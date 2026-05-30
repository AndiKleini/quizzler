package com.quizzler.payment.repository;

import com.quizzler.payment.domain.Payment;
import com.quizzler.payment.domain.PaymentCancellation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentCancellationRepository extends JpaRepository<PaymentCancellation, Long> {

    boolean existsByPayment(Payment payment);
}
