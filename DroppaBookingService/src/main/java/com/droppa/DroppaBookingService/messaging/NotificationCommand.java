package com.droppa.DroppaBookingService.messaging;

import java.time.Instant;
import java.util.UUID;

public record NotificationCommand(
        String eventId,
        String recipient,
        String subject,
        String message,
        String correlationId,
        Instant occurredAt
) {
    public static NotificationCommand forBooking(BookingEvent event) {
        return new NotificationCommand(
                UUID.randomUUID().toString(),
                event.userEmail(),
                "Booking update: " + event.eventType(),
                "Booking " + event.bookingId() + " is now " + event.status() + ".",
                event.eventId(),
                Instant.now()
        );
    }
}
