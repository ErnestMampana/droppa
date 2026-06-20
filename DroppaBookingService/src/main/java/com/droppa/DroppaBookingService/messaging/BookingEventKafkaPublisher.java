package com.droppa.DroppaBookingService.messaging;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BookingEventKafkaPublisher {

    private final KafkaJsonPublisher publisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishBookingEvent(BookingEvent event) {
        publisher.send(KafkaTopics.BOOKING_EVENTS, event.bookingId(), event);

        if (event.userEmail() != null && !event.userEmail().isBlank()) {
            publisher.send(
                    KafkaTopics.NOTIFICATION_COMMANDS,
                    event.userEmail(),
                    NotificationCommand.forBooking(event)
            );
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publishPaymentRequest(PaymentRequested event) {
        publisher.send(KafkaTopics.PAYMENT_COMMANDS, event.bookingId(), event);
    }
}
