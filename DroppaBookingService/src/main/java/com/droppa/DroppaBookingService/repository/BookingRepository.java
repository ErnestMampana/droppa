/**
 * 
 */
package com.droppa.DroppaBookingService.repository;

import java.util.List;
import java.util.Optional;

import com.droppa.DroppaBookingService.enums.BookingStatus;
import com.droppa.DroppaBookingService.entity.Booking;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * @author Ernest Mampana
 *
 */
public interface BookingRepository extends JpaRepository<Booking, Integer> {
	Optional<Booking> findByBookingId(String bookingId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select b from Booking b where b.bookingId = :bookingId")
	Optional<Booking> findByBookingIdForUpdate(@Param("bookingId") String bookingId);

	Optional<List<Booking>> findAllByStatus(BookingStatus status);
	
	List<Booking> findAllByAssinedDriver(String driverId);

	List<Booking> findAllByStatusAndUserId(BookingStatus status, String userId);
	
	List<Booking> findAllByUserId(String status);
	
	Optional<Booking> deleteByBookingId(String bookingId); 
}
