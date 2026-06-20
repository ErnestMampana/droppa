package com.droppa.DroppaDriverService.services;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.droppa.DroppaDriverService.dto.BookingClientResponse;
import com.droppa.DroppaDriverService.entity.DriverAccount;
import com.droppa.DroppaDriverService.entity.DriverBookingOffer;
import com.droppa.DroppaDriverService.entity.DriverNotification;
import com.droppa.DroppaDriverService.enums.AccountStatus;
import com.droppa.DroppaDriverService.enums.DriverAvailability;
import com.droppa.DroppaDriverService.exception.ClientException;
import com.droppa.DroppaDriverService.interfaces.BookingServiceClient;
import com.droppa.DroppaDriverService.messaging.BookingEvent;
import com.droppa.DroppaDriverService.repositories.DriverAccountRepository;
import com.droppa.DroppaDriverService.repositories.DriverBookingOfferRepository;
import com.droppa.DroppaDriverService.repositories.DriverNotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverBookingOfferService {

    private static final List<String> ACTIVE_ASSIGNMENT_STATUSES =
            List.of("ACCEPTED", "IN_TRANSACT");
    private static final List<DateTimeFormatter> TIME_FORMATTERS = List.of(
            DateTimeFormatter.ofPattern("H:mm"),
            DateTimeFormatter.ofPattern("HH:mm"),
            DateTimeFormatter.ofPattern("h:mm a"),
            DateTimeFormatter.ofPattern("hh:mm a")
    );

    private final DriverBookingOfferRepository offerRepository;
    private final DriverNotificationRepository notificationRepository;
    private final DriverAccountRepository driverAccountRepository;
    private final BookingServiceClient bookingServiceClient;

    @Value("${app.driver-notifications.resend-after:PT2M}")
    private Duration resendAfter;

    @Transactional
    public void handleBookingEvent(BookingEvent event) {
        LocalDateTime scheduledAt = parseScheduledAt(event);
        DriverBookingOffer offer = offerRepository.findByBookingId(event.bookingId())
                .orElseGet(() -> DriverBookingOffer.from(event, scheduledAt));

        if (offer.getId() != null) {
            offer.apply(event, scheduledAt);
        }

        offerRepository.save(offer);
        syncDriverAvailability(event);

        if (offer.isOpenForDriverNotification()) {
            notifyEligibleDrivers(offer);
        }
    }

    @Transactional
    public BookingClientResponse acceptBooking(String bookingId, String driverEmail) {
        String email = requireEmail(driverEmail);
        DriverAccount driver = getDriverForUpdate(email);

        if (!driver.isOnlineForOffers()) {
            throw new ClientException("Driver must be online and available to accept bookings");
        }

        DriverBookingOffer offer = offerRepository.findByBookingIdForUpdate(bookingId)
                .orElseThrow(() -> new ClientException("Booking offer not found"));

        if (!offer.isOpenForDriverNotification()) {
            throw new ClientException("Booking is no longer available");
        }

        ensureDriverCanAccept(driver, offer);

        BookingClientResponse booking = bookingServiceClient.acceptBooking(bookingId, email);
        offer.markAccepted(email, booking.status());
        return booking;
    }

    @Transactional
    public BookingClientResponse startDelivery(String bookingId, String driverEmail) {
        String email = requireEmail(driverEmail);
        DriverAccount driver = getDriverForUpdate(email);
        DriverBookingOffer offer = requireAssignedOfferForUpdate(bookingId, email);

        BookingClientResponse booking = bookingServiceClient.startDelivery(bookingId, email);
        driver.startTransit();
        offer.markAccepted(email, booking.status());
        return booking;
    }

    @Transactional
    public BookingClientResponse completeDelivery(String bookingId, String driverEmail) {
        String email = requireEmail(driverEmail);
        DriverAccount driver = getDriverForUpdate(email);
        DriverBookingOffer offer = requireAssignedOfferForUpdate(bookingId, email);

        BookingClientResponse booking = bookingServiceClient.completeBooking(bookingId, email);
        driver.completeTransit();
        offer.markAccepted(email, booking.status());
        return booking;
    }

    @Transactional(readOnly = true)
    public List<DriverNotification> getNotifications(String driverEmail) {
        return notificationRepository.findByDriverEmailOrderByCreatedAtDesc(requireEmail(driverEmail));
    }

    @Scheduled(fixedDelayString = "${app.driver-notifications.resend-interval-ms:120000}")
    @Transactional
    public void resendOpenBookingNotifications() {
        Instant cutoff = Instant.now().minus(resendAfter);
        offerRepository.findOpenOffersReadyForNotification(cutoff)
                .forEach(this::notifyEligibleDrivers);
    }

    private void notifyEligibleDrivers(DriverBookingOffer offer) {
        List<DriverNotification> notifications = driverAccountRepository
                .findConfirmedDriversByStatusAndAvailability(AccountStatus.ACTIVE, DriverAvailability.ONLINE)
                .stream()
                .filter(driver -> canReceiveOffer(driver, offer))
                .map(driver -> DriverNotification.create(driver.getEmail(), offer))
                .toList();

        if (!notifications.isEmpty()) {
            notificationRepository.saveAll(notifications);
        }

        offer.markNotificationSent(notifications.size());
        log.info("Created {} driver notifications for booking {}", notifications.size(), offer.getBookingId());
    }

    private boolean canReceiveOffer(DriverAccount driver, DriverBookingOffer offer) {
        return driver.isOnlineForOffers() && !hasScheduleConflict(driver.getEmail(), offer);
    }

    private void ensureDriverCanAccept(DriverAccount driver, DriverBookingOffer offer) {
        if (hasScheduleConflict(driver.getEmail(), offer)) {
            throw new ClientException("Driver has a conflicting delivery near this booking time");
        }
    }

    private boolean hasScheduleConflict(String driverEmail, DriverBookingOffer candidate) {
        List<DriverBookingOffer> assignments = offerRepository.findDriverAssignments(
                driverEmail,
                ACTIVE_ASSIGNMENT_STATUSES
        );

        if (assignments.stream().anyMatch(assignment -> "IN_TRANSACT".equals(assignment.getStatus()))) {
            return true;
        }

        LocalDateTime candidateStart = candidate.getScheduledAt();
        if (candidateStart == null) {
            return false;
        }

        return assignments.stream()
                .map(DriverBookingOffer::getScheduledAt)
                .filter(existingStart -> existingStart != null)
                .anyMatch(existingStart -> Math.abs(Duration.between(existingStart, candidateStart).toMinutes()) < 30);
    }

    private void syncDriverAvailability(BookingEvent event) {
        if (event.driverId() == null || event.driverId().isBlank() || event.status() == null) {
            return;
        }

        driverAccountRepository.findByEmailForUpdate(event.driverId()).ifPresent(driver -> {
            if ("IN_TRANSACT".equals(event.status())) {
                driver.startTransit();
            } else if ("COMPLETE".equals(event.status()) && driver.getAvailabilityStatus() == DriverAvailability.IN_TRANSIT) {
                driver.completeTransit();
            }
        });
    }

    private DriverBookingOffer requireAssignedOfferForUpdate(String bookingId, String driverEmail) {
        DriverBookingOffer offer = offerRepository.findByBookingIdForUpdate(bookingId)
                .orElseThrow(() -> new ClientException("Booking offer not found"));

        if (!offer.isAssignedTo(driverEmail)) {
            throw new ClientException("Booking is not assigned to this driver");
        }

        return offer;
    }

    private DriverAccount getDriverForUpdate(String email) {
        return driverAccountRepository.findByEmailForUpdate(email)
                .orElseThrow(() -> new ClientException("Driver not found"));
    }

    private LocalDateTime parseScheduledAt(BookingEvent event) {
        if (event.bookingDate() == null || event.time() == null || event.time().isBlank()) {
            return null;
        }

        String rawTime = event.time().trim().toUpperCase();
        for (DateTimeFormatter formatter : TIME_FORMATTERS) {
            try {
                return LocalDateTime.of(event.bookingDate(), LocalTime.parse(rawTime, formatter));
            } catch (DateTimeParseException ignored) {
            }
        }

        log.warn("Unable to parse booking time '{}' for booking {}", event.time(), event.bookingId());
        return null;
    }

    private String requireEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ClientException("Authenticated driver email is required");
        }

        return email.trim();
    }
}
