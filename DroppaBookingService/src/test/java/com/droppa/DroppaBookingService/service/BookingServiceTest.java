package com.droppa.DroppaBookingService.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.droppa.DroppaBookingService.dto.BookingDTO;
import com.droppa.DroppaBookingService.dto.PaymentDAO;
import com.droppa.DroppaBookingService.entity.Booking;
import com.droppa.DroppaBookingService.enums.BookingStatus;
import com.droppa.DroppaBookingService.enums.VehicleType;
import com.droppa.DroppaBookingService.exceptions.BookingAccessDeniedException;
import com.droppa.DroppaBookingService.messaging.PaymentRequested;
import com.droppa.DroppaBookingService.messaging.PaymentResult;
import com.droppa.DroppaBookingService.repository.BookingRepository;
import com.droppa.DroppaBookingService.repository.DropDetailsrepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private DropDetailsrepository dropRepository;
    @Mock
    private PartyService partyService;
    @Mock
    private BookingPricingService bookingPricingService;
    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void createsUnpaidBookingUsingServerCalculatedPrice() {
        BookingDTO request = validBooking();
        BigDecimal serverPrice = new BigDecimal("725.00");
        when(bookingPricingService.calculatePrice(request)).thenReturn(serverPrice);
        when(partyService.generateTrackNumber()).thenReturn("GAU123456");
        when(bookingRepository.save(any(Booking.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Booking booking = bookingService.createBooking(request, "customer@example.com");

        assertEquals(serverPrice, booking.getPrice());
        assertEquals(BookingStatus.AWAITING_PAYMENT, booking.getStatus());
        assertEquals("customer@example.com", booking.getUserId());
        verify(bookingPricingService).calculatePrice(request);
    }

    @Test
    void allowsOwnerToViewBookingById() {
        Booking booking = bookingOwnedBy("customer@example.com");
        when(bookingRepository.findByBookingId("booking-1")).thenReturn(java.util.Optional.of(booking));

        Booking result = bookingService.getBookingByIdForAuthenticatedUser(
                "booking-1",
                "CUSTOMER@example.com");

        assertSame(booking, result);
    }

    @Test
    void allowsAssignedDriverToViewBookingById() {
        Booking booking = Booking.builder()
                .bookingId("booking-1")
                .userId("customer@example.com")
                .assinedDriver("driver@example.com")
                .build();
        when(bookingRepository.findByBookingId("booking-1")).thenReturn(java.util.Optional.of(booking));

        Booking result = bookingService.getBookingByIdForAuthenticatedUser(
                "booking-1",
                "driver@example.com");

        assertSame(booking, result);
    }

    @Test
    void rejectsUnrelatedUserViewingBookingById() {
        Booking booking = bookingOwnedBy("customer@example.com");
        when(bookingRepository.findByBookingId("booking-1")).thenReturn(java.util.Optional.of(booking));

        assertThrows(
                BookingAccessDeniedException.class,
                () -> bookingService.getBookingByIdForAuthenticatedUser(
                        "booking-1",
                        "attacker@example.com"));
    }

    @Test
    void rejectsUserListingAnotherUsersBookings() {
        assertThrows(
                BookingAccessDeniedException.class,
                () -> bookingService.getBookingsByUserId(
                        "victim@example.com",
                        "attacker@example.com"));

        verify(bookingRepository, never()).findAllByUserId(any());
    }

    @Test
    void rejectsDriverListingAnotherDriversBookings() {
        assertThrows(
                BookingAccessDeniedException.class,
                () -> bookingService.getBookingsForAuthenticatedDriver(
                        "victim@example.com",
                        "attacker@example.com"));

        verify(bookingRepository, never()).findAllByAssinedDriver(any());
    }

    @Test
    void rejectsCancellingAnotherUsersBooking() {
        Booking booking = bookingOwnedBy("victim@example.com");
        when(bookingRepository.findByBookingId("booking-1")).thenReturn(java.util.Optional.of(booking));

        assertThrows(
                BookingAccessDeniedException.class,
                () -> bookingService.cancelBooking("booking-1", "attacker@example.com"));

        assertEquals(BookingStatus.AWAITING_PAYMENT, booking.getStatus());
    }

    @Test
    void paymentPublishesCommandAndMovesBookingToProcessing() {
        Booking booking = bookingOwnedBy("customer@example.com");
        PaymentDAO payment = PaymentDAO.builder()
                .bookingId("booking-1")
                .paymentType("WALLET")
                .build();
        when(bookingRepository.findByBookingId("booking-1")).thenReturn(java.util.Optional.of(booking));

        bookingService.makePayment(payment, "customer@example.com");

        verify(eventPublisher).publishEvent(any(PaymentRequested.class));
        assertEquals(BookingStatus.PAYMENT_PROCESSING, booking.getStatus());
    }

    @Test
    void completedPaymentResultMovesBookingToAwaitingDriver() {
        Booking booking = bookingOwnedBy("customer@example.com");
        booking.requestPayment(
                PaymentDAO.builder().bookingId("booking-1").paymentType("WALLET").build(),
                "request-1"
        );
        when(bookingRepository.findByBookingId("booking-1")).thenReturn(java.util.Optional.of(booking));

        bookingService.handlePaymentResult(new PaymentResult(
                "result-1",
                "request-1",
                "booking-1",
                "customer@example.com",
                "COMPLETED",
                null,
                java.time.Instant.now()
        ));

        assertEquals(BookingStatus.AWAITING_DRIVER, booking.getStatus());
    }

    @Test
    void failedPaymentResultRestoresAwaitingPayment() {
        Booking booking = bookingOwnedBy("customer@example.com");
        booking.requestPayment(
                PaymentDAO.builder().bookingId("booking-1").paymentType("WALLET").build(),
                "request-1"
        );
        when(bookingRepository.findByBookingId("booking-1")).thenReturn(java.util.Optional.of(booking));

        bookingService.handlePaymentResult(new PaymentResult(
                "result-1",
                "request-1",
                "booking-1",
                "customer@example.com",
                "FAILED",
                "Insufficient balance",
                java.time.Instant.now()
        ));

        assertEquals(BookingStatus.AWAITING_PAYMENT, booking.getStatus());
    }

    private Booking bookingOwnedBy(String email) {
        return Booking.builder()
                .bookingId("booking-1")
                .userId(email)
                .status(BookingStatus.AWAITING_PAYMENT)
                .price(new BigDecimal("725.00"))
                .build();
    }

    private BookingDTO validBooking() {
        return BookingDTO.builder()
                .pickupadress("Pickup")
                .dropoffadress("Dropoff")
                .date(LocalDate.now().plusDays(1))
                .vehicle(VehicleType.ONE_TON)
                .pickUpName("Pickup contact")
                .pickUpCellphone("0712345678")
                .dropOffName("Dropoff contact")
                .dropOffPhone("0787654321")
                .paymentType("WALLET")
                .loads(1)
                .labours(1)
                .itemsToBeDelivered("Furniture")
                .time("10:00")
                .build();
    }
}
