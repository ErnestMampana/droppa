/**
 * 
 */
package com.droppa.clone.droppa.services;

import java.util.Optional;

import com.droppa.clone.droppa.dto.PaymentDAO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.droppa.clone.droppa.common.ClientException;
import com.droppa.clone.droppa.dto.RentalDTO;
import com.droppa.clone.droppa.enums.AccountStatus;
import com.droppa.clone.droppa.enums.BookingStatus;
import com.droppa.clone.droppa.enums.RentalStatus;
import com.droppa.clone.droppa.models.Booking;
import com.droppa.clone.droppa.models.Rental;
import com.droppa.clone.droppa.models.UserAccount;
import com.droppa.clone.droppa.repositories.RentalRepository;
import com.droppa.clone.droppa.repositories.UserAccountRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Ernest Mampana
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RentalService {

	private final RentalRepository rentalRepository;

	private final UserAccountRepository userRepository;

	private final PartyService partyService;

	private final UserService userService;
	
	private final ModelMapper modelMapper;

	public Rental createRental(RentalDTO rentalData) {

		log.info("======================================== Requesting to create a rental booking");
		String rentalId;

		UserAccount user = userService.getUserByEmail(rentalData.getUserId());

		if (user.getStatus().equals(AccountStatus.ACTIVE)) {

			rentalId = partyService.randomChars(15);

			boolean found = true;
			while (found) {
				Optional<Rental> bookingOptional = rentalRepository.findByRentalId(rentalId);
				if (bookingOptional.isPresent()) {
					rentalId = partyService.randomChars(15);
				} else {
					found = false;
				}
			}
			modelMapper.getConfiguration().setAmbiguityIgnored(true);
			Rental rental = modelMapper.map(rentalData, Rental.class);
			rental.setStatus(RentalStatus.AWAITING_PAYMENT);
			rental.setRentalId(rentalId);
			
			rentalRepository.save(rental);

			return rental;
		} else {
			throw new ClientException("User " + rentalData.getUserId() + " is not active");
		}

	}

	public Rental getBookingById(String bookingId) {
		Optional<Rental> rentalOptional = rentalRepository.findByRentalId(bookingId);

		if (rentalOptional.isPresent()) {
			return rentalOptional.get();
		} else {
			throw new ClientException("Rental booking not found");
		}
	}

	@Transactional
	public Rental makePayments(PaymentDAO payment) {
		UserAccount user = userService.getUserByEmail(payment.getUserId());
		Rental rentalBooking = getBookingById(payment.getBookingId());
		if (rentalBooking.getUserId().equals(payment.getUserId())) {
			if (payment.getPaymentType().equals("Wallet")) {
				if(user.getPerson().getWalletBalance() < payment.getBookingPrice())
					throw new ClientException("Insufficient funds");
				user.getPerson().setWalletBalance(user.getPerson().getWalletBalance() - payment.getBookingPrice());
			}
			rentalBooking.setPaymentType(payment.getPaymentType());
			rentalBooking.setPromoCodeUsed(payment.getUsedPromo());
			rentalBooking.setStatus(RentalStatus.AWAITING_DRIVER);
			rentalBooking.setPrice(payment.getBookingPrice());
			return rentalBooking;
		} else {
			throw new ClientException("Something went wrong, please try again");
		}
	}

}
