/**
 * 
 */
package com.droppa.clone.droppa.services;

import java.util.Optional;

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
			
			Rental rental = modelMapper.map(rentalData, Rental.class);
			rental.setStatus(RentalStatus.AWAITING_PAYMENT);
			rental.setRentalId(rentalId);
			
			rentalRepository.save(rental);
//			Rental rental = Rental.builder().userId(rentalData.getUserId()).streetAddress(rentalData.getStreetAddress())
//					.postalCode(rentalData.getPostalCode()).suburb(rentalData.getSuburb())
//					.province(rentalData.getProvince()).complexName(rentalData.getComplexName())
//					.unitNumber(rentalData.getUnitNumber()).startDate(rentalData.getStartDate())
//					.endDate(rentalData.getEndDate()).truckType(rentalData.getTruckType()).price(rentalData.getPrice())
//					.companyName(rentalData.getCompanyName()).contactPerson(rentalData.getContactPerson())
//					.mobileNumber(rentalData.getMobileNumber()).rentalBunch(rentalData.getRentalBunch())
//					.labours(rentalData.getLabours()).noDays(rentalData.getNoDays())
//					.instruction(rentalData.getInstruction()).status(RentalStatus.AWAITING_PAYMENT).rentalId(rentalId)
//					.build();

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

}
