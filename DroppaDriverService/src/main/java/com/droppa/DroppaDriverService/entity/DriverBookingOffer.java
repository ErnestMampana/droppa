package com.droppa.DroppaDriverService.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

import com.droppa.DroppaDriverService.messaging.BookingEvent;

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
@Table(name = "driver_booking_offer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DriverBookingOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String bookingId;

    private String eventType;
    private String userEmail;
    private String status;
    private BigDecimal amount;
    private String driverId;
    private LocalDateTime scheduledAt;
    private String pickupAddress;
    private String dropOffAddress;
    private String vehicleType;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant lastNotificationAt;
    private int notificationCount;

    public static DriverBookingOffer from(BookingEvent event, LocalDateTime scheduledAt) {
        DriverBookingOffer offer = new DriverBookingOffer();
        offer.bookingId = event.bookingId();
        offer.createdAt = Instant.now();
        offer.apply(event, scheduledAt);
        return offer;
    }

    public void apply(BookingEvent event, LocalDateTime scheduledAt) {
        this.eventType = event.eventType();
        this.userEmail = event.userEmail();
        this.status = event.status();
        this.amount = event.amount();
        this.driverId = normalize(event.driverId());
        this.scheduledAt = scheduledAt;
        this.pickupAddress = event.pickupAddress();
        this.dropOffAddress = event.dropOffAddress();
        this.vehicleType = event.vehicleType();
        this.updatedAt = Instant.now();
    }

    public void markNotificationSent(int sentCount) {
        this.lastNotificationAt = Instant.now();
        this.notificationCount += sentCount;
        this.updatedAt = Instant.now();
    }

    public void markAccepted(String driverEmail, String status) {
        this.driverId = normalize(driverEmail);
        this.status = status;
        this.updatedAt = Instant.now();
    }

    public boolean isOpenForDriverNotification() {
        return "AWAITING_DRIVER".equals(status) && (driverId == null || driverId.isBlank());
    }

    public boolean isAssignedTo(String driverEmail) {
        return driverId != null
                && driverEmail != null
                && driverId.trim().equalsIgnoreCase(driverEmail.trim());
    }

    private String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
