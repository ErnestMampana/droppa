/**
 * 
 */
package com.droppa.DroppaBookingService.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.droppa.DroppaBookingService.entity.Rental;

/**
 * @author Ernest Mampana
 *
 */
public interface RentalRepository extends JpaRepository<Rental, Integer> {

	Optional<Rental> findByRentalId(String rentalId);

}
