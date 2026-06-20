package com.droppa.DroppaBookingService.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import com.droppa.DroppaBookingService.dto.PaymentDAO;
import com.droppa.DroppaBookingService.enums.BookingStatus;

import com.droppa.DroppaBookingService.exceptions.BookingAccessDeniedException;
import com.droppa.DroppaBookingService.exceptions.BookingException;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String bookingId;

    private String pickUpAddess;

    private String userId;

    private String dropOffAdress;

    private LocalDate bookingDate;

    private BigDecimal price;

    private String assinedDriver;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private int loads;

    private int labours;

    private String trackNumber;

    private String itemsToBeDelivered;

    private String vehicleType;

    private String paymentType;

    private String time;

    @OneToOne
    private DropDetails dropDetails;

    private String promoCodeUsed;

    private String paymentRequestId;

    public static Booking create(
            String userId,
            String pickupAddress,
            String dropOffAddress,
            LocalDate bookingDate,
            String time,
            BigDecimal price,
            int loads,
            int labours,
            String items,
            String vehicleType,
            String paymentType,
            DropDetails dropDetails,
            String trackNumber) {

        return Booking.builder()
                .bookingId(UUID.randomUUID().toString())
                .userId(userId)
                .pickUpAddess(pickupAddress)
                .dropOffAdress(dropOffAddress)
                .bookingDate(bookingDate)
                .time(time)
                .price(price)
                .loads(loads)
                .labours(labours)
                .itemsToBeDelivered(items)
                .vehicleType(vehicleType)
                .paymentType(paymentType)
                .dropDetails(dropDetails)
                .trackNumber(trackNumber)
                .status(BookingStatus.AWAITING_PAYMENT)
                .build();
    }

    public void cancel(String userId) {

        validateOwnership(userId);

        switch (status) {

            case CANCELLED -> throw new BookingException("Booking already cancelled");

            case COMPLETE -> throw new BookingException("Completed booking cannot be cancelled");

            case IN_TRANSACT -> throw new BookingException("Booking already in transit");

            case PAYMENT_PROCESSING -> throw new BookingException("Booking payment is being processed");

            default -> this.status = BookingStatus.CANCELLED;
        }
    }

    public void requireOwnedBy(String authenticatedUserEmail) {
        validateOwnership(authenticatedUserEmail);
    }

    public void requireAssignedTo(String driverEmail) {
        if (!identitiesMatch(assinedDriver, driverEmail)) {
            throw new BookingAccessDeniedException(
                    "You cannot modify a booking assigned to another driver");
        }
    }

    public boolean canBeViewedBy(String authenticatedEmail) {
        return identitiesMatch(userId, authenticatedEmail)
                || identitiesMatch(assinedDriver, authenticatedEmail);
    }

    public void assignDriver(String driverId) {

        if (status != BookingStatus.AWAITING_DRIVER) {
            throw new BookingException(
                    "Booking not available for driver assignment");
        }

        this.assinedDriver = driverId;
        this.status = BookingStatus.ACCEPTED;
    }

    public void startDelivery() {

        if (status != BookingStatus.ACCEPTED) {
            throw new BookingException(
                    "Booking must be accepted first");
        }

        this.status = BookingStatus.IN_TRANSACT;
    }

    public void complete() {

        if (status != BookingStatus.IN_TRANSACT) {
            throw new BookingException(
                    "Booking must be in transit first");
        }

        this.status = BookingStatus.COMPLETE;
    }

    public void requestPayment(PaymentDAO payment, String requestId) {
        validatePaymentAllowed();

        this.paymentType = payment.getPaymentType();
        this.promoCodeUsed = payment.getUsedPromo();
        this.paymentRequestId = requestId;
        this.status = BookingStatus.PAYMENT_PROCESSING;
    }

    public void completePayment(String requestId) {
        validateCurrentPaymentRequest(requestId);
        this.status = BookingStatus.AWAITING_DRIVER;
    }

    public void failPayment(String requestId) {
        validateCurrentPaymentRequest(requestId);
        this.status = BookingStatus.AWAITING_PAYMENT;
    }

    private void validateOwnership(String userId) {

        if (!identitiesMatch(this.userId, userId)) {
            throw new BookingAccessDeniedException(
                    "You cannot modify a booking belonging to another user");
        }
    }

    private boolean identitiesMatch(String firstIdentity, String secondIdentity) {
        return firstIdentity != null
                && secondIdentity != null
                && firstIdentity.trim().equalsIgnoreCase(secondIdentity.trim());
    }

    private void validatePaymentAllowed() {

        switch (status) {

            case CANCELLED -> throw new BookingException(
                    "Cancelled booking cannot be paid");

            case COMPLETE -> throw new BookingException(
                    "Completed booking cannot be paid");

            case AWAITING_DRIVER -> throw new BookingException(
                    "Booking already paid");

            case PAYMENT_PROCESSING -> throw new BookingException(
                    "Payment already processing");

            default -> {
            }
        }
    }

    private void validateCurrentPaymentRequest(String requestId) {
        if (paymentRequestId == null || !paymentRequestId.equals(requestId)) {
            throw new BookingException("Payment result does not match the current request");
        }

        if (status != BookingStatus.PAYMENT_PROCESSING) {
            throw new BookingException("Booking is not awaiting a payment result");
        }
    }
}
