package com.droppa.DroppaBookingService.messaging;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentRequested(
        String eventId,
        String bookingId,
        String userEmail,
        BigDecimal amount,
        String paymentType,
        String promoCode,
        Instant occurredAt
) {
}
