package com.droppa.DroppaDriverService.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookingClientResponse(
        String bookingId,
        String userId,
        LocalDate bookingDate,
        String time,
        BigDecimal price,
        String assinedDriver,
        String status,
        String vehicleType,
        String pickUpAddess,
        String dropOffAdress
) {
}
