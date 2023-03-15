/**
 *
 */
package com.droppa.clone.droppa.services;

import com.droppa.clone.droppa.common.ClientException;
import com.droppa.clone.droppa.dto.BookingDTO;
import com.droppa.clone.droppa.dto.CoordinatesDTO;
import com.droppa.clone.droppa.enums.AccountStatus;
import com.droppa.clone.droppa.enums.BookingStatus;
import com.droppa.clone.droppa.models.Adress;
import com.droppa.clone.droppa.models.Booking;
import com.droppa.clone.droppa.models.DropDetails;
import com.droppa.clone.droppa.models.UserAccount;
import com.droppa.clone.droppa.repositories.AddressRespository;
import com.droppa.clone.droppa.repositories.BookingRepository;
import com.droppa.clone.droppa.repositories.DropDetailsrepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * @author Ernest Mampana
 *
 */
@Service
@RequiredArgsConstructor
public class BookingService {

    private final PartyService partyService;

    private final BookingRepository bookingRepository;

    private final AddressRespository addressRespository;

    private final DropDetailsrepository dropRepo;

    private final UserService userService;

    public Booking createBooking(BookingDTO bookingDto) {

        String bookingId;

        // Person person = userService.getUserById(bookingDto.userId).getOwner();

//		if (person.getId() == null)
//			throw new ClientException("Only registered users can create a booking");

        var adress = Adress.builder()
                .pickUpStandNumber(bookingDto.getPickUpStandNumber())
                .pickUpStreetName(bookingDto.getPickUpStreetName())
                .pickUpSuburb(bookingDto.getPickUpSuburb())
                .pickUpProvince(bookingDto.getPickUpProvince())
                .pickUpPostalCode(bookingDto.getPickUpPostalCode())
                .dropOffStandNumber(bookingDto.getDropOffStandNumber())
                .dropOffStreetName(bookingDto.getDropOffStreetName())
                .dropOffSuburb(bookingDto.getDropOffSuburb())
                .dropOffProvince(bookingDto.getDropOffProvince())
                .dropOffPostalCode(bookingDto.getDropOffPostalCode()).build();


        var dropDetails = DropDetails.builder()
                .pickUpName(bookingDto.getPickUpName())
                .pickUpSurname(bookingDto.getPickUpSurname())
                .pickUpContact(bookingDto.getPickUpContact())
                .dropOffName(bookingDto.getDropOffName())
                .dropOffSurname(bookingDto.getDropOffSurname())
                .dropOffContact(bookingDto.getDropOffContact()).build();


        bookingId = partyService.randomChars(15);

        boolean found = true;
        while (found) {
            Optional<Booking> bookingOptional = bookingRepository.findByBookingId(bookingId);
            if (bookingOptional.isPresent()) {
                bookingId = partyService.randomChars(15);
            } else {
                found = false;
            }
        }

        var booking = Booking.builder()
                .bookingId(bookingId)
                .adressDetails(adress)
                .userId(bookingDto.getUserId())
                .dropDetails(dropDetails)
                .bookingDate(bookingDto.getDate())
                .price(0)
                .assinedDriver(null)
                .status(BookingStatus.AWAITING_DRIVER).build();


        UserAccount userAccount = userService.getUserByEmail(bookingDto.getUserId());

        if (userAccount.getStatus().equals(AccountStatus.ACTIVE)) {
            dropRepo.save(dropDetails);
            addressRespository.save(adress);
            bookingRepository.save(booking);
            return booking;
        } else {
            if (userAccount.getStatus().equals(AccountStatus.AWAITING_CONFIRMATION)) {
                throw new ClientException("Please confirm your account first.");
            } else if (userAccount.getStatus().equals(AccountStatus.AWAITING_PWD_RESET)) {
                throw new ClientException("You haven't set confirmed your new password");
            } else {
                throw new ClientException(
                        "Your account has been suspended please contact Droppa Clone for re-activation.");
            }
        }

    }
    
    //Change input parameters to latlong and calculate distance between two geo locations
    public double requestPrice(CoordinatesDTO coordinates) {
    	double basePrice = 80.00;
    	//TODO: Calculate distance and assign new price
    	return basePrice;
    }

    public List<Booking> getAllBookings() {
		return bookingRepository.findAll();
    }

    public Booking getBookingById(String bookingId) {
        Optional<Booking> bookingOptional = bookingRepository.findByBookingId(bookingId);

        if (bookingOptional.isPresent()) {
            return bookingOptional.get();
        } else {
            throw new ClientException("booking not found");
        }
    }

    public List<Booking> getBookingsByStatus(BookingStatus status) {

        Optional<List<Booking>> bookingOptional = bookingRepository.findAllByStatus(status);

        if (bookingOptional.isPresent()) {
            List<Booking> bookings = bookingOptional.get();
            return bookings;
        } else {
            throw new ClientException("No bookings matching '" + status + "' status");
        }

    }

    public List<Booking> getBookingsByStatusForUser(BookingStatus status, String userId) {

        Optional<List<Booking>> bookingOptional = bookingRepository.findAllByStatusAndUserId(status, userId);

        if (bookingOptional.isPresent()) {
            List<Booking> bookings = bookingOptional.get();
            return bookings;
        } else {
            throw new ClientException(
                    "No bookings matching '" + status + "' status for provided user '" + userId + "'.");
        }

    }

    public List<Booking> getBookingsByDriverId(String driverId) {
        Optional<List<Booking>> bookingOptional = bookingRepository.findAllByAssinedDriver(driverId);

        if (bookingOptional.isPresent()) {
            List<Booking> bookings = bookingOptional.get();
            return bookings;
        } else {
            throw new ClientException("No bookings matching assigned driver '" + driverId + "'.");
        }
    }

    public List<Booking> getBookingByUserId(String userId) {
        Optional<List<Booking>> bookingOptional = bookingRepository.findAllByUserId(userId);

        if (bookingOptional.isPresent()) {
            List<Booking> bookings = bookingOptional.get();
            return bookings;
        } else {
            throw new ClientException("No bookings matching assigned driver '" + userId + "'.");
        }
    }

    @Transactional
    public Booking cancelBooking(String bookingId, String userId) {
        Booking booking = getBookingById(bookingId);
        if (booking.getBookingId() != null && booking.getStatus() != BookingStatus.CANCELLED
                && booking.getUserId().equals(userId) && booking.getStatus() != BookingStatus.COMPLETE) {

            booking.setStatus(BookingStatus.CANCELLED);
            return booking;
        } else {
            if (booking.getStatus().equals(BookingStatus.COMPLETE)) {
                throw new ClientException("This is booking is completed and cant be edited.");
            } else {
                throw new ClientException("Something went wrong, please try again");
            }
        }
    }

}
