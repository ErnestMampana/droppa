package com.droppa.DroppaBookingService.service;

import com.droppa.DroppaBookingService.entity.Booking;

import com.droppa.DroppaBookingService.exceptions.BookingAccessDeniedException;
import com.droppa.DroppaBookingService.exceptions.BookingException;
import com.droppa.DroppaBookingService.repository.BookingRepository;

import com.droppa.DroppaBookingService.interfaces.UserServiceClient;
import com.droppa.DroppaBookingService.dto.BookingDTO;

import com.droppa.DroppaBookingService.dto.PaymentDAO;
import com.droppa.DroppaBookingService.dto.PersonClient;

import com.droppa.DroppaBookingService.entity.DropDetails;
import com.droppa.DroppaBookingService.repository.DropDetailsrepository;
//import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

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
    private final UserServiceClient userClient;
    private final BookingPricingService bookingPricingService;

	public Booking createBooking(BookingDTO dto,String email) {

		DropDetails dropDetails = buildDropDetails(dto);
		var bookingPrice = bookingPricingService.calculatePrice(dto);

		Booking booking = Booking.create(email, dto.pickupadress(), dto.dropoffadress(), dto.date(),
				dto.time(), bookingPrice, dto.loads(), dto.labours(), dto.itemsToBeDelivered(),
				dto.vehicle().name(), dto.paymentType(), dropDetails, partyService.generateTrackNumber());

		dropRepository.save(dropDetails);

		return bookingRepository.save(booking);
	}

	public Booking cancelBooking(String bookingId, String authenticatedEmail) {

		Booking booking = getBookingById(bookingId);

		booking.cancel(requireAuthenticatedEmail(authenticatedEmail));

		return booking;
	}

	public Booking makePayment(PaymentDAO payment, String authenticatedEmail) {
		String email = requireAuthenticatedEmail(authenticatedEmail);
		Booking booking = getBookingById(payment.getBookingId());
		booking.requireOwnedBy(email);

		PersonClient user = userClient.getUserByEmail(email);

		booking.pay(user, payment);

		return booking;
	}

	public Booking assignDriver(String bookingId, String driverId) {

		Booking booking = getBookingById(bookingId);

		booking.assignDriver(driverId);

		return booking;
	}

	public Booking startDelivery(String bookingId) {

		Booking booking = getBookingById(bookingId);

		booking.startDelivery();

		return booking;
	}

	public Booking completeBooking(String bookingId) {

		Booking booking = getBookingById(bookingId);

		booking.complete();

		return booking;
	}

	public Booking getBookingById(String bookingId) {

		return bookingRepository.findByBookingId(bookingId).orElseThrow(
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
}
