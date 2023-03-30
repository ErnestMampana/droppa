/**
 * 
 */
package com.droppa.clone.droppa.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.droppa.clone.droppa.models.Rates;

/**
 * @author Ernest Mampana
 *
 */
public interface RatesRepository extends JpaRepository<Rates, Integer> {
	Optional<Rates> findByVehicleType(String vehicleType);
}
