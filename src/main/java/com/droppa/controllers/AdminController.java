package com.droppa.controllers;

import com.droppa.DroppaBookingService.entity.Booking;
import com.droppa.dto.CreatePromDTO;
import com.droppa.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;

	@PutMapping("/suspenddriver/{driverId}")
	public ResponseEntity<String> suspendDriver(@PathVariable("driverId") String driverId) {
		return new ResponseEntity<String>(adminService.suspendDriver(driverId),HttpStatus.ACCEPTED);
	}

	@PutMapping("/activatedriver/{driverId}")
	public ResponseEntity<String> activateDriver(@PathVariable("driverId") String driverId) {
		return new ResponseEntity<String>(adminService.confirmDriver(driverId),HttpStatus.ACCEPTED);
	}

	@PutMapping("/assigndriver/{bookingId}/{driverId}")
	public ResponseEntity<Booking> asignBookingToDriver(@PathVariable("bookingId") String bookingId,
														@PathVariable("driverId") String driverId) {
		return new ResponseEntity<Booking>(adminService.asignBookingToDriver(bookingId, driverId),HttpStatus.ACCEPTED);
	}

//	@DeleteMapping("/deletebooking/{bookingId}")
//	public ResponseEntity<String> deleteBooking(@PathVariable("bookingId") String bokingId) {
//		String message = adminService.deleteBooking(bokingId);
//		return new ResponseEntity<String>(message,HttpStatus.OK);
//	}

	@PutMapping("/suspenduser/{useremail}")
	public ResponseEntity<String> suspendUser(@PathVariable("useremail") String userId) {
		return new ResponseEntity<String>(adminService.suspendUser(userId),HttpStatus.ACCEPTED);
	}
	
	@PutMapping("/activateuser/{useremail}")
	public ResponseEntity<String> activateUser(@PathVariable("useremail") String userId) {
		return new ResponseEntity<String>(adminService.activateUser(userId),HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/createpromocode")
	public ResponseEntity<String> createPromoCode(@RequestBody CreatePromDTO promoDto){
		return new ResponseEntity<String>(adminService.generatePromoCode(promoDto),HttpStatus.ACCEPTED);
	}
	
	@DeleteMapping("/deleteBooking/{bookingId}")
	public ResponseEntity<String> deleteBooking(@PathVariable("bookingId") String bookingId){
		return new ResponseEntity<String>(adminService.deleteBooking(bookingId),HttpStatus.ACCEPTED);
	}
}
