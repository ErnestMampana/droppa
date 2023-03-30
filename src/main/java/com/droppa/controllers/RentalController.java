package com.droppa.clone.droppa.controllers;

import com.droppa.clone.droppa.dto.PaymentDAO;
import com.droppa.clone.droppa.dto.RentalDTO;
import com.droppa.clone.droppa.models.Booking;
import com.droppa.clone.droppa.models.Rental;
import com.droppa.clone.droppa.services.RentalService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/rental")
@RequiredArgsConstructor
public class RentalController {

	private final RentalService rentalService;

	@PostMapping("/createRentalBooking")
	public ResponseEntity<Rental> createRentalBooking(@RequestBody RentalDTO rentalDTO) {
		return ResponseEntity.ok(rentalService.createRental(rentalDTO));
	}

	@PutMapping("/makePayment")
	public ResponseEntity<Rental> makePayments(@RequestBody PaymentDAO payment) {
		Rental rentalBooking = rentalService.makePayments(payment);
		return new ResponseEntity<Rental>(rentalBooking, HttpStatus.OK);
	}

	@GetMapping("/viewAllBookings")
	public ResponseEntity<List<Rental>> viewAllBookings() {
		return ResponseEntity.ok(rentalService.getAllRentals());
	}
}
