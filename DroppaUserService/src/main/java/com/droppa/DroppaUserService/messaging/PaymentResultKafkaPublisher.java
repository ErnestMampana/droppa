package com.droppa.DroppaUserService.messaging;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentResultKafkaPublisher {

    private final KafkaJsonPublisher publisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(PaymentResult event) {
        publisher.send(KafkaTopics.PAYMENT_EVENTS, event.bookingId(), event);
    }
}
