/**
 * 
 */
package com.droppa.DroppaBookingService.controller;

import java.util.List;

import com.droppa.DroppaBookingService.entity.Booking;
import com.droppa.DroppaBookingService.service.BookingService;
import com.droppa.DroppaBookingService.dto.BookingDTO;
import com.droppa.DroppaBookingService.dto.PaymentDAO;
import com.droppa.DroppaBookingService.dto.PromoCodeDTO;
import com.droppa.DroppaBookingService.service.PartyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ernest Mampana
 *
 */
@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
public class BookingController {

	private final BookingService bookingService;
	private final PartyService partyService;

//	// @Secured
//	@PostMapping("/book")
//	public ResponseEntity<Booking> createBooking(@RequestBody BookingDTO bookingDto) {
//		Booking book = bookingService.createBooking(bookingDto);
//		return new ResponseEntity<Booking>(book, HttpStatus.OK);
//	}
	
	@PostMapping("/create-booking")
	public Booking createBooking(
	        @Valid @RequestBody BookingDTO dto,
	        @RequestHeader("X-User-Email") String email) {

	    return bookingService.createBooking(dto, email);
	}

//	@GetMapping("/bookingbystatus/{status}")
//	public List<Booking> getBookingByStatus(@PathVariable("status") BookingStatus status) {
//		return bookingService.getBookingsByStatus(status);
//	}
//
//	@GetMapping("/bookingbystatusforuser/{status}/{userid}")
//	public List<Booking> getBookingsByStatusForUser(@PathVariable("status") BookingStatus status,
//			@PathVariable("userid") String userid) {
//		return bookingService.getBookingsByStatusForUser(status, userid);
//	}


	@GetMapping("/bookingById/{id}")
	public ResponseEntity<Booking> getBookingById(
			@PathVariable String id,
			@RequestHeader("X-User-Email") String authenticatedEmail) {
		Booking booking = bookingService.getBookingByIdForAuthenticatedUser(id, authenticatedEmail);
		return new ResponseEntity<Booking>(booking, HttpStatus.OK);
	}

	@GetMapping("/bookingByDriverId/{id}")
	public List<Booking> getBookingByDriverId(
			@PathVariable String id,
			@RequestHeader("X-User-Email") String authenticatedEmail) {
		return bookingService.getBookingsForAuthenticatedDriver(id, authenticatedEmail);
	}

	@GetMapping("/bookingByUserId/{id}")
	public List<Booking> getBookingByUserId(
			@PathVariable String id,
			@RequestHeader("X-User-Email") String authenticatedEmail) {
		return bookingService.getBookingsByUserId(id, authenticatedEmail);
	}

	@PutMapping("/cancelBooking/{bookingId}")
	public ResponseEntity<Booking> cancelBooking(
			@PathVariable String bookingId,
			@RequestHeader("X-User-Email") String authenticatedEmail) {
		Booking cBooking = bookingService.cancelBooking(bookingId, authenticatedEmail);
		return new ResponseEntity<Booking>(cBooking, HttpStatus.OK);
	}

	@PutMapping("/makePayment")
	public ResponseEntity<Booking> makePayments(
			@Valid @RequestBody PaymentDAO payment,
			@RequestHeader("X-User-Email") String authenticatedEmail) {
		Booking cBooking = bookingService.makePayment(payment, authenticatedEmail);
		return new ResponseEntity<Booking>(cBooking, HttpStatus.ACCEPTED);
	}

	@PutMapping("/accept/{bookingId}")
	public ResponseEntity<Booking> acceptBooking(
			@PathVariable String bookingId,
			@RequestHeader("X-User-Email") String driverEmail) {
		Booking booking = bookingService.assignDriver(bookingId, driverEmail);
		return new ResponseEntity<Booking>(booking, HttpStatus.ACCEPTED);
	}

	@PutMapping("/startDelivery/{bookingId}")
	public ResponseEntity<Booking> startDelivery(
			@PathVariable String bookingId,
			@RequestHeader("X-User-Email") String driverEmail) {
		Booking booking = bookingService.startDelivery(bookingId, driverEmail);
		return new ResponseEntity<Booking>(booking, HttpStatus.ACCEPTED);
	}

	@PutMapping("/completeBooking/{bookingId}")
	public ResponseEntity<Booking> completeBooking(
			@PathVariable String bookingId,
			@RequestHeader("X-User-Email") String driverEmail) {
		Booking booking = bookingService.completeBooking(bookingId, driverEmail);
		return new ResponseEntity<Booking>(booking, HttpStatus.OK);
	}

//	@PostMapping("/getprice")
//	public ResponseEntity<Double> requestPrice(@RequestBody CoordinatesDTO coordinates) {
//		return new ResponseEntity<Double>(bookingService.requestPrice(coordinates), HttpStatus.OK);
//	}

	@PostMapping("/applypromocode")
	public ResponseEntity<Double> applyPromCode(@RequestBody PromoCodeDTO promocodeDto) {
		return new ResponseEntity<Double>(partyService.applyPromocode(promocodeDto),HttpStatus.OK);
	}

}
