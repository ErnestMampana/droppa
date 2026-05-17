/**
 *
 */
package com.droppa.services;

import com.droppa.DroppaUserService.entity.UserAccount;
import com.droppa.DroppaUserService.enums.AccountStatus;
import com.droppa.DroppaUserService.repository.UserAccountRepository;
import com.droppa.DroppaUserService.service.UserService;
import com.droppa.common.ClientException;
import com.droppa.dto.BookingDTO;
import com.droppa.dto.CoordinatesDTO;
import com.droppa.dto.PaymentDAO;
import com.droppa.enums.BookingStatus;
import com.droppa.enums.PaymentMethod;
import com.droppa.models.Adress;
import com.droppa.models.Booking;
import com.droppa.models.DropDetails;
import com.droppa.repositories.AddressRespository;
import com.droppa.repositories.BookingRepository;
import com.droppa.repositories.DropDetailsrepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.catalina.mapper.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author Ernest Mampana
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

	private final PartyService partyService;

	private final BookingRepository bookingRepository;

	private final AddressRespository addressRespository;

	private final DropDetailsrepository dropRepo;

	private final UserService userService;

	private final UserAccountRepository userAccountRepository;
	
	


	@Transactional
	public Booking createBooking(BookingDTO bookingDto) {

		validateActiveUser(bookingDto.getUserId());

		DropDetails dropDetails = buildDropDetails(bookingDto);

		Booking booking = buildBooking(bookingDto, dropDetails);

		saveBooking(booking, dropDetails);

		return booking;

	}

	// Change input parameters to latlong and calculate distance between two geo
	// locations
	public double requestPrice(CoordinatesDTO coordinates) {
		double basePrice = 80.00;
		// TODO: Calculate distance and assign new price
		return basePrice;
	}

	public List<Booking> getAllBookings() {
		return bookingRepository.findAll();
	}

	public Booking getBookingById(String bookingId) {
		
		return bookingRepository.findByBookingId(bookingId).
				orElseThrow(()-> new ClientException("booking not found"));

	}

	public List<Booking> getBookingsByStatus(BookingStatus status) {

		return bookingRepository.findAllByStatus(status).
				orElseThrow(() -> new ClientException("No booking matching"+ status+"status"));

	}

	public List<Booking> getBookingsByStatusForUser(BookingStatus status, String userId) {

		return bookingRepository.findAllByStatusAndUserId(status, userId).
				orElseThrow(() -> new ClientException("No bookings matching '" + status + "' status for provided user '" + userId + "'."));

	}

	public List<Booking> getBookingsByDriverId(String driverId) {
		return bookingRepository.findAllByAssinedDriver(driverId).
				orElseThrow(() -> new ClientException("No bookings matching assigned driver '" + driverId + "'."));
	}

	public List<Booking> getBookingByUserId(String userId) {
		return bookingRepository.findAllByUserId(userId).
				orElseThrow(() -> new ClientException("No bookings matching assigned driver '" + userId + "'."));
	}

	
	@Transactional
	public Booking cancelBooking(String bookingId, String userId) {

	    Booking booking = getBookingById(bookingId);

	    validateBookingOwnership(booking, userId);

	    validateBookingCanBeCancelled(booking);

	    booking.setStatus(BookingStatus.CANCELLED);

	    return booking;
	}

//	@Transactional
//	public Booking makePayments(PaymentDAO payment) {
//		UserAccount user = userService.getUserByEmail(payment.getUserId());
//		Booking booking = getBookingById(payment.getBookingId());
//		if (booking.getUserId().equals(payment.getUserId())) {
//			if (payment.getPaymentType().equals("Wallet")) {
//				if(user.getPerson().getWalletBalance() < payment.getBookingPrice())
//					throw new ClientException("Insufficient funds");
//				user.getPerson().setWalletBalance(user.getPerson().getWalletBalance() - payment.getBookingPrice());
//			}				
//			booking.setPaymentType(payment.getPaymentType());
//			booking.setPromoCodeUsed(payment.getUsedPromo());
//			booking.setStatus(BookingStatus.AWAITING_DRIVER);
//			booking.setPrice(payment.getBookingPrice());
//			return booking;
//		} else {
//			throw new ClientException("Something went wrong, please try again");
//		}
//	}
	
	
	@Transactional
	public Booking makePayment(PaymentDAO payment) {

	    UserAccount user =
	            userService.getUserByEmail(payment.getUserId());

	    Booking booking =
	            getBookingById(payment.getBookingId());

	    validateBookingOwnership(booking, user);

	    validateBookingPaymentAllowed(booking);

	    processPayment(user, booking, payment);

	    booking.setPromoCodeUsed(payment.getUsedPromo());

	    booking.setStatus(BookingStatus.AWAITING_DRIVER);

	    return booking;
	}
	
	
	private DropDetails buildDropDetails(BookingDTO bookingDto) {
		return DropDetails.builder().dropOffNames(bookingDto.getDropOffName())
				.dropOffContact(bookingDto.getDropOffPhone()).pickUpNames(bookingDto.getPickUpName())
				.pickUpContact(bookingDto.getPickUpCellphone()).build();
	}
	
	private Booking buildBooking(BookingDTO bookingDto,DropDetails dropDetails) {
		return Booking.builder().bookingId(UUID.randomUUID().toString()).pickUpAddess(bookingDto.getPickupadress())
				.userId(bookingDto.getUserId()).dropOffAdress(bookingDto.getDropoffadress())
				.bookingDate(bookingDto.getDate()).time(bookingDto.getTime()).price(bookingDto.getBookingPrice())
				.itemsToBeDelivered(bookingDto.getItemsToBeDelivered()).labours(bookingDto.getLabours())
				.paymentType(bookingDto.getPaymentType()).dropDetails(dropDetails).loads(bookingDto.getLoads())
				.assinedDriver(null).vehicleType(bookingDto.getVehicle()).status(bookingDto.isPaid() ? 
						BookingStatus.AWAITING_DRIVER : BookingStatus.AWAITING_PAYMENT)
				.trackNumber(partyService.generateTracknumber()).build();
	}
	
	private void saveBooking(Booking booking,DropDetails dropDetails) {
		log.info("Booking {} has been created", booking.getBookingId());
		dropRepo.save(dropDetails);
		bookingRepository.save(booking);
	}
	
	
	private void validateActiveUser(String email) {

	    UserAccount user = userAccountRepository.findByEmail(email)
	            .orElseThrow(() ->
	                    new ClientException("User account not found"));

	    if (!AccountStatus.ACTIVE.equals(user.getStatus())) {
	        throw new ClientException("User account is not active");
	    }
	}
	
	private void validateBookingOwnership(
	        Booking booking,
	        String userId) {

	    if (!booking.getUserId().equals(userId)) {
	        throw new ClientException(
	                "You are not allowed to cancel this booking");
	    }
	}
	
	private void validateBookingCanBeCancelled(Booking booking) {

	    switch (booking.getStatus()) {

	        case CANCELLED:
	            throw new ClientException(
	                    "Booking is already cancelled");

	        case COMPLETE:
	            throw new ClientException(
	                    "Completed bookings cannot be cancelled");

	        case IN_TRANSACT:
	            throw new ClientException(
	                    "Booking already in transit cannot be cancelled");

	        default:
	            break;
	    }
	}
	
	private void processPayment(
	        UserAccount user,
	        Booking booking,
	        PaymentDAO payment) {

	    if (PaymentMethod.WALLET.equals(payment.getPaymentType())) {

	        BigDecimal balance =
	                user.getPerson().getWalletBalance();

	        BigDecimal bookingPrice =
	                booking.getPrice();

	        if (balance.compareTo(bookingPrice) < 0) {
	            throw new ClientException("Insufficient funds");
	        }

	        user.getPerson().setWalletBalance(
	                balance.subtract(bookingPrice)
	        );
	    }
	}
	
	private void validateBookingOwnership(
	        Booking booking,
	        UserAccount user) {

	    if (!booking.getUserId().equals(user.getEmail())) {
	        throw new ClientException(
	                "You are not allowed to pay for this booking");
	    }
	}
	
	
	private void validateBookingPaymentAllowed(Booking booking) {

	    switch (booking.getStatus()) {

	        case CANCELLED:
	            throw new ClientException(
	                    "Cancelled booking cannot be paid");

	        case COMPLETE:
	            throw new ClientException(
	                    "Completed booking cannot be paid");

	        case AWAITING_DRIVER:
	            throw new ClientException(
	                    "Booking already paid");

	        default:
	            break;
	    }
	}

}
