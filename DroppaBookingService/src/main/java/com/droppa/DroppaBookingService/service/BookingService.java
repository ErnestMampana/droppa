package com.droppa.DroppaBookingService.service;

import com.droppa.DroppaBookingService.entity.Booking;

import com.droppa.DroppaBookingService.exceptions.BookingAccessDeniedException;
import com.droppa.DroppaBookingService.exceptions.BookingException;
import com.droppa.DroppaBookingService.repository.BookingRepository;

import com.droppa.DroppaBookingService.dto.BookingDTO;

import com.droppa.DroppaBookingService.dto.PaymentDAO;

import com.droppa.DroppaBookingService.entity.DropDetails;
import com.droppa.DroppaBookingService.enums.BookingStatus;
import com.droppa.DroppaBookingService.messaging.BookingEvent;
import com.droppa.DroppaBookingService.messaging.PaymentRequested;
import com.droppa.DroppaBookingService.messaging.PaymentResult;
import com.droppa.DroppaBookingService.repository.DropDetailsrepository;
//import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



/**
 * @author Ernest Mampana
 *
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;
    private final DropDetailsrepository dropRepository;
    private final PartyService partyService;
    private final BookingPricingService bookingPricingService;
    private final ApplicationEventPublisher eventPublisher;

	public Booking createBooking(BookingDTO dto,String email) {

		DropDetails dropDetails = buildDropDetails(dto);
		var bookingPrice = bookingPricingService.calculatePrice(dto);

		Booking booking = Booking.create(email, dto.pickupadress(), dto.dropoffadress(), dto.date(),
				dto.time(), bookingPrice, dto.loads(), dto.labours(), dto.itemsToBeDelivered(),
				dto.vehicle().name(), dto.paymentType(), dropDetails, partyService.generateTrackNumber());

		dropRepository.save(dropDetails);

		Booking savedBooking = bookingRepository.save(booking);
		publishBookingEvent(savedBooking, "BOOKING_CREATED");
		return savedBooking;
	}

	public Booking cancelBooking(String bookingId, String authenticatedEmail) {

		Booking booking = getBookingById(bookingId);

		booking.cancel(requireAuthenticatedEmail(authenticatedEmail));
		publishBookingEvent(booking, "BOOKING_CANCELLED");

		return booking;
	}

	public Booking makePayment(PaymentDAO payment, String authenticatedEmail) {
		String email = requireAuthenticatedEmail(authenticatedEmail);
		Booking booking = getBookingById(payment.getBookingId());
		booking.requireOwnedBy(email);

		String requestEventId = UUID.randomUUID().toString();
		booking.requestPayment(payment, requestEventId);

		eventPublisher.publishEvent(new PaymentRequested(
				requestEventId,
				booking.getBookingId(),
				email,
				booking.getPrice(),
				payment.getPaymentType(),
				payment.getUsedPromo(),
				Instant.now()
		));
		publishBookingEvent(booking, "PAYMENT_REQUESTED");

		return booking;
	}

	public Booking assignDriver(String bookingId, String driverId) {

		Booking booking = getBookingByIdForUpdate(bookingId);

		booking.assignDriver(driverId);
		publishBookingEvent(booking, "DRIVER_ASSIGNED");

		return booking;
	}

	public Booking startDelivery(String bookingId, String authenticatedDriverEmail) {

		Booking booking = getBookingByIdForUpdate(bookingId);
		booking.requireAssignedTo(requireAuthenticatedEmail(authenticatedDriverEmail));

		booking.startDelivery();
		publishBookingEvent(booking, "DELIVERY_STARTED");

		return booking;
	}

	public Booking completeBooking(String bookingId, String authenticatedDriverEmail) {

		Booking booking = getBookingByIdForUpdate(bookingId);
		booking.requireAssignedTo(requireAuthenticatedEmail(authenticatedDriverEmail));

		booking.complete();
		publishBookingEvent(booking, "DELIVERY_COMPLETED");

		return booking;
	}

	public void handlePaymentResult(PaymentResult result) {
		Booking booking = getBookingById(result.bookingId());

		if (result.requestEventId() == null
				|| !result.requestEventId().equals(booking.getPaymentRequestId())
				|| booking.getStatus() != BookingStatus.PAYMENT_PROCESSING) {
			log.warn(
					"Ignoring stale or duplicate payment result {} for booking {}",
					result.eventId(),
					result.bookingId()
			);
			return;
		}

		if ("COMPLETED".equalsIgnoreCase(result.status())) {
			booking.completePayment(result.requestEventId());
			publishBookingEvent(booking, "PAYMENT_COMPLETED");
			return;
		}

		if ("FAILED".equalsIgnoreCase(result.status())) {
			booking.failPayment(result.requestEventId());
			publishBookingEvent(booking, "PAYMENT_FAILED");
			return;
		}

		throw new BookingException("Unsupported payment result status: " + result.status());
	}

	public Booking getBookingById(String bookingId) {

		return bookingRepository.findByBookingId(bookingId).orElseThrow(
				() -> new BookingException("Booking not found"));
	}

	private Booking getBookingByIdForUpdate(String bookingId) {

		return bookingRepository.findByBookingIdForUpdate(bookingId).orElseThrow(
				() -> new BookingException("Booking not found"));
	}

	public Booking getBookingByIdForAuthenticatedUser(String bookingId, String authenticatedEmail) {
		Booking booking = getBookingById(bookingId);

		if (!booking.canBeViewedBy(requireAuthenticatedEmail(authenticatedEmail))) {
			throw new BookingAccessDeniedException("You do not have access to this booking");
		}

		return booking;
	}
	
	public List<Booking> getBookingsForAuthenticatedDriver(String requestedDriverId, String authenticatedEmail) {
		String email = requireMatchingIdentity(requestedDriverId, authenticatedEmail);

	    return bookingRepository
	            .findAllByAssinedDriver(email);
	}
	
	public List<Booking> getBookingsByUserId(String requestedUserId, String authenticatedEmail){
		return bookingRepository.findAllByUserId(requireMatchingIdentity(requestedUserId, authenticatedEmail));
	}

	private DropDetails buildDropDetails(BookingDTO dto) {

		return DropDetails.builder().
				dropOffNames(dto.dropOffName()).
				dropOffContact(dto.dropOffPhone()).
				pickUpNames(dto.pickUpName()).
				pickUpContact(dto.pickUpCellphone()).build();
	}

	private String requireMatchingIdentity(String requestedIdentity, String authenticatedEmail) {
		String email = requireAuthenticatedEmail(authenticatedEmail);

		if (requestedIdentity == null || !requestedIdentity.trim().equalsIgnoreCase(email)) {
			throw new BookingAccessDeniedException("You cannot access bookings belonging to another user");
		}

		return email;
	}

	private String requireAuthenticatedEmail(String authenticatedEmail) {
		if (authenticatedEmail == null || authenticatedEmail.isBlank()) {
			throw new BookingAccessDeniedException("Authenticated user email is required");
		}

		return authenticatedEmail.trim();
	}

	private void publishBookingEvent(Booking booking, String eventType) {
		eventPublisher.publishEvent(BookingEvent.from(booking, eventType));
	}
}
