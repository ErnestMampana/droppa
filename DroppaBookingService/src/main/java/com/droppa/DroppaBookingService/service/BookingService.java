package com.droppa.DroppaBookingService.service;

import com.droppa.DroppaBookingService.entity.Booking;

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

	public Booking createBooking(BookingDTO dto,String email) {

		DropDetails dropDetails = buildDropDetails(dto);

		Booking booking = Booking.create(email, dto.pickupadress(), dto.dropoffadress(), dto.date(),
				dto.time(), dto.bookingPrice(), dto.loads(), dto.labours(), dto.itemsToBeDelivered(),
				dto.vehicle(), dto.paymentType(), dropDetails, dto.isPaid(), partyService.generateTrackNumber());

		dropRepository.save(dropDetails);

		return bookingRepository.save(booking);
	}

	public Booking cancelBooking(String bookingId, String userId) {

		Booking booking = getBookingById(bookingId);

		booking.cancel(userId);

		return booking;
	}

	public Booking makePayment(PaymentDAO payment) {

		PersonClient user = userClient.getUserByEmail(payment.getUserId());

		Booking booking = getBookingById(payment.getBookingId());

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
	
	public List<Booking> getBookingsForAuthenticatedDriver(String driverIdFromToken) {

	    return bookingRepository
	            .findAllByAssinedDriver(driverIdFromToken);
	}
	
	public List<Booking> getBookingsByUserId(String userId){
		return bookingRepository.findAllByUserId(userId);
	}

	private DropDetails buildDropDetails(BookingDTO dto) {

		return DropDetails.builder().
				dropOffNames(dto.dropOffName()).
				dropOffContact(dto.dropOffPhone()).
				pickUpNames(dto.pickUpName()).
				pickUpContact(dto.pickUpCellphone()).build();
	}
}
