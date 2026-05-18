/**
 * 
 */
package com.droppa.DroppaDriverService.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.droppa.DroppaDriverService.entity.Rates;

/**
 * @author Ernest Mampana
 *
 */
public interface RatesRepository extends JpaRepository<Rates, Integer> {
	Optional<Rates> findByVehicleType(String vehicleType);
}
