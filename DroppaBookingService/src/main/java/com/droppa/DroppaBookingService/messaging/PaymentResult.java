package com.droppa.DroppaBookingService.messaging;

import java.time.Instant;

public record PaymentResult(
        String eventId,
        String requestEventId,
        String bookingId,
        String userEmail,
        String status,
        String reason,
        Instant occurredAt
) {
}
