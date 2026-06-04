/**
 * 
 */
package com.droppa.DroppaBookingService.service;

import java.util.List;
import java.util.Optional;

import com.droppa.DroppaBookingService.exceptions.BookingException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.droppa.DroppaBookingService.interfaces.UserServiceClient;
import com.droppa.DroppaBookingService.dto.PersonClient;
import com.droppa.DroppaBookingService.dto.RentalDTO;
import com.droppa.DroppaBookingService.enums.RentalStatus;
import com.droppa.DroppaBookingService.entity.Rental;
import com.droppa.DroppaBookingService.repository.RentalRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Ernest Mampana
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RentalService {

	private final UserServiceClient userServiceClient;
	
	private final RentalRepository rentalRepository;

	private final PartyService partyService;
	
	private final ModelMapper modelMapper;

	public Rental createRental(RentalDTO rentalData) {

		log.info("Requesting to create a rental booking");
		String rentalId;

		PersonClient user = userServiceClient.getUserByEmail(rentalData.getUserId());
		
		//Only return active user from user service

	//	if (user.getStatus().equals(AccountStatus.ACTIVE)) {

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
//		} else {
//			throw new ClientException("User " + rentalData.getUserId() + " is not active");
//		}

	}

	public Rental getBookingById(String bookingId) {
		Optional<Rental> rentalOptional = rentalRepository.findByRentalId(bookingId);

		if (rentalOptional.isPresent()) {
			return rentalOptional.get();
		} else {
			throw new BookingException("Rental booking not found");
		}
	}

//	@Transactional
//	public Rental makePayments(PaymentDAO payment) {
//		PersonClient user = userServiceClient.getUserByEmail(payment.getUserId());
//		Rental rentalBooking = getBookingById(payment.getBookingId());
//		if (rentalBooking.getUserId().equals(payment.getUserId())) {
//			if (payment.getPaymentType().equals("Wallet")) {
//				if(user.getPerson().getWalletBalance().compareTo(payment.getBookingPrice()) < 0)
//					throw new ClientException("Insufficient funds");
//	
//
//				user.getPerson().setWalletBalance(user.getPerson().getWalletBalance()
//		                .subtract(payment.getBookingPrice()));
//			}
//			rentalBooking.setPaymentType(payment.getPaymentType());
//			rentalBooking.setPromoCodeUsed(payment.getUsedPromo());
//			rentalBooking.setStatus(RentalStatus.AWAITING_DRIVER);
//			rentalBooking.setPrice(payment.getBookingPrice());
//			return rentalBooking;
//		} else {
//			throw new ClientException("Something went wrong, please try again");
//		}
//	}
	
	public List<Rental> getAllRentals(){
		return rentalRepository.findAll();
	}

}
