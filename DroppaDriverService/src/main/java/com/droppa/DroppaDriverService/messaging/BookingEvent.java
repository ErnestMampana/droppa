package com.droppa.DroppaDriverService.messaging;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record BookingEvent(
        String eventId,
        String eventType,
        String bookingId,
        String userEmail,
        String status,
        BigDecimal amount,
        String driverId,
        LocalDate bookingDate,
        String time,
        String pickupAddress,
        String dropOffAddress,
        String vehicleType,
        Instant occurredAt
) {
}
