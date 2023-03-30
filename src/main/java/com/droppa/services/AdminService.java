package com.droppa.clone.droppa.services;

import java.time.LocalDate;
import java.util.List;

import com.droppa.clone.droppa.common.ClientException;
import com.droppa.clone.droppa.dto.CreatePromDTO;
import com.droppa.clone.droppa.enums.AccountStatus;
import com.droppa.clone.droppa.enums.BookingStatus;
import com.droppa.clone.droppa.models.Booking;
import com.droppa.clone.droppa.models.DriverAccount;
import com.droppa.clone.droppa.models.PromoCode;
import com.droppa.clone.droppa.models.UserAccount;
import com.droppa.clone.droppa.repositories.AddressRespository;
import com.droppa.clone.droppa.repositories.BookingRepository;
import com.droppa.clone.droppa.repositories.DropDetailsrepository;
import com.droppa.clone.droppa.repositories.PromoCodeRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final UserService userService;

	private final DriverService driverService;

	private final BookingService bookingService;

	private final BookingRepository bookingRepository;

	private final AddressRespository addressRespository;

	private final PartyService partyService;

	private final DropDetailsrepository dropDetailsrepository;

	private final PromoCodeRepository promoRepository;

//	public boolean validateToken(String token) {
//
//		System.out.println("========================== " + authService.extractedToken());
//
//		boolean valid = false;
//
//		try {
//			if (token == null || token.trim().equals(""))
//				valid = false;
//
//			if (token.equals(authService.extractedToken()))
//				valid = true;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//
//			return false;
//		}
//
//		return valid;
//
//	}
	
	public String deleteBooking(String bookingId) {
		String message = "Booking not found";
		Booking booking = bookingService.getBookingById(bookingId);
		bookingRepository.delete(booking);
		return message;
	}

	@Transactional
	public String suspendUser(String userId) {

		String message = "User not found";

		UserAccount userAccount = userService.getUserByEmail(userId);

		if (userAccount.getStatus() == AccountStatus.SUSPENDED) {
			message = "User " + userAccount.getEmail() + " is already suspended";
			throw new ClientException("User " + userAccount.getPerson().getUserName() + " "
					+ userAccount.getPerson().getSurname() + " is already suspended");
		}

		if (userAccount.getStatus().equals(AccountStatus.AWAITING_CONFIRMATION))
			throw new ClientException("User '" + userAccount.getPerson().getUserName() + " "
					+ userAccount.getPerson().getSurname() + "' not avtivated");

		userAccount.setStatus(AccountStatus.SUSPENDED);

		message = "User '" + userAccount.getPerson().getUserName() + " " + userAccount.getPerson().getSurname()
				+ "' has been Suspended";

		return message;
	}

	@Transactional
	public String confirmDriver(String driverId) {

		String message = "Driver not found";

		DriverAccount driver = driverService.getDriverByEmail(driverId);

		if (driver.getStatus() != AccountStatus.AWAITING_CONFIRMATION && driver.getStatus() != AccountStatus.SUSPENDED)
			throw new ClientException("Driver is already confirmed");

		driver.setStatus(AccountStatus.ACTIVE);

		return message;
	}

	@Transactional
	public String suspendDriver(String driverId) {
		String message = "Driver not found";

		DriverAccount driverAcc = driverService.getDriverByEmail(driverId);
		if (driverAcc.getStatus().equals(AccountStatus.SUSPENDED))
			throw new ClientException("Driver " + driverAcc.getDriver().getName() + " "
					+ driverAcc.getDriver().getSurname() + " is already suspended");

		driverAcc.setStatus(AccountStatus.SUSPENDED);

		message = "Driver Suspended";

		return message;
	}

	@Transactional
	public Booking asignBookingToDriver(String bookingId, String driverId) {

		Booking booking = bookingService.getBookingById(bookingId);

		List<Booking> driverBookings = bookingService.getBookingsByDriverId(driverId);

		if (booking.getBookingId() == null)
			throw new ClientException("Booking with id '" + bookingId + "' does not exist");

		for (int i = 0; i <= driverBookings.size() - 1; i++) {
			if (driverBookings.get(i).getBookingDate().equals(booking.getBookingDate())
					&& driverBookings.get(i).getStatus() == BookingStatus.RESERVED)
				throw new ClientException("Driver has a booking during this time");

		}

		booking.setAssinedDriver(driverId);
		booking.setStatus(BookingStatus.RESERVED);
		booking.setStatus(BookingStatus.RESERVED);

		return booking;
	}

	@Transactional
	public String activateUser(String userId) {
		UserAccount userAcc = userService.getUserByEmail(userId);

		if (userAcc.getStatus().equals(AccountStatus.SUSPENDED)) {
			userAcc.setStatus(AccountStatus.ACTIVE);
			return "User account activated";
		} else {
			throw new ClientException("Could not activate user");
		}

	}

	@Transactional
	public String generatePromoCode(CreatePromDTO promoDto) {
		PromoCode promo = PromoCode.builder().promoCode(partyService.generatePromoCode())
				.promoCount(promoDto.promoCount).numberOfTimesUsed(0).discountPrice(promoDto.discountPrice)
				.expiration(promoDto.expiration).build();
		PromoCode savedPromo = promoRepository.save(promo);
		return savedPromo.getPromoCode();
	}

//	public String deleteBooking(String bookingId) {
//		String message = "Booking not found";
//		Booking booking = bookingService.getBookingById(bookingId);
//		if (booking.getStatus().equals(BookingStatus.IN_TRANSACT)) {
//			throw new ClientException("This booking cant be deleted, driver is already i transit");
//		} else {
//			bookingRepository.deleteByBookingId(bookingId);
//			dropDetailsrepository.deleteById(booking.getDropDetails().getId());
//			addressRespository.deleteById(booking.getAdressDetails().getId());
//
//			message = "booking deleted";
//		}
//		return message;
//	}
}
