package com.droppa.DroppaDriverService.entity;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "driver_notification")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DriverNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String driverEmail;

    @Column(nullable = false)
    private String bookingId;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000, nullable = false)
    private String message;

    private boolean readFlag;
    private Instant createdAt;

    public static DriverNotification create(String driverEmail, DriverBookingOffer offer) {
        DriverNotification notification = new DriverNotification();
        notification.driverEmail = driverEmail;
        notification.bookingId = offer.getBookingId();
        notification.title = "New booking available";
        notification.message = buildMessage(offer);
        notification.readFlag = false;
        notification.createdAt = Instant.now();
        return notification;
    }

    public void markRead() {
        this.readFlag = true;
    }

    private static String buildMessage(DriverBookingOffer offer) {
        String when = offer.getScheduledAt() == null
                ? "the requested time"
                : offer.getScheduledAt().toString();
        return "Booking " + offer.getBookingId()
                + " is available for " + when
                + " from " + nullSafe(offer.getPickupAddress())
                + " to " + nullSafe(offer.getDropOffAddress()) + ".";
    }

    private static String nullSafe(String value) {
        return value == null || value.isBlank() ? "unknown location" : value;
    }
}
