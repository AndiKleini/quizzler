package com.quizzler.api.messaging;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.quizzler.api.service.QuizAttemptPurchaseService;

@SpringBootTest
public class PaymentConfirmationListenerTests {
    
    private static final String PRODUCT_ID = "PRODUCT_ID";
    private static final String TRANSACTION_ID = "TRANSACTION_ID";
    @Mock
    private QuizAttemptPurchaseService quizAttemptPurchaseService;

    @Test
    public void receive_confirmation_forward_to_repository(){
        PaymentConfirmationListener instanceUnderTest = 
            new PaymentConfirmationListener(quizAttemptPurchaseService);

        instanceUnderTest.handleConfirmation(
            new PaymentConfirmationEvent(TRANSACTION_ID, PRODUCT_ID));
        
        verify(quizAttemptPurchaseService).confirmPurchase(PRODUCT_ID, TRANSACTION_ID);
    }
}
