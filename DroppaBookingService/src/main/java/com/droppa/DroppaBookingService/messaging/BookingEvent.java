package com.droppa.DroppaBookingService.messaging;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import com.droppa.DroppaBookingService.entity.Booking;

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
    public static BookingEvent from(Booking booking, String eventType) {
        return new BookingEvent(
                UUID.randomUUID().toString(),
                eventType,
                booking.getBookingId(),
                booking.getUserId(),
                booking.getStatus().name(),
                booking.getPrice(),
                booking.getAssinedDriver(),
                booking.getBookingDate(),
                booking.getTime(),
                booking.getPickUpAddess(),
                booking.getDropOffAdress(),
                booking.getVehicleType(),
                Instant.now()
        );
    }
}
