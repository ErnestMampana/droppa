/**
 * 
 */
package com.droppa.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.droppa.models.Rental;

/**
 * @author Ernest Mampana
 *
 */
public interface RentalRepository extends JpaRepository<Rental, Integer> {

	Optional<Rental> findByRentalId(String rentalId);

}
