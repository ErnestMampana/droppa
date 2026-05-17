/**
 * 
 */
package com.droppa.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.droppa.models.Rates;

/**
 * @author Ernest Mampana
 *
 */
public interface RatesRepository extends JpaRepository<Rates, Integer> {
	Optional<Rates> findByVehicleType(String vehicleType);
}
