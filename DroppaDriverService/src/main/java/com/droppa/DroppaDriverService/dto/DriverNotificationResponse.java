package com.droppa.DroppaDriverService.dto;

import java.time.Instant;

import com.droppa.DroppaDriverService.entity.DriverNotification;

public record DriverNotificationResponse(
        Long id,
        String bookingId,
        String title,
        String message,
        boolean read,
        Instant createdAt
) {
    public static DriverNotificationResponse from(DriverNotification notification) {
        return new DriverNotificationResponse(
                notification.getId(),
                notification.getBookingId(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isReadFlag(),
                notification.getCreatedAt()
        );
    }
}
