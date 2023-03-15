/**
 * 
 */
package com.droppa.clone.droppa.repositories;

import java.util.Optional;

import com.droppa.clone.droppa.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * @author Ernest Mampana
 *
 */
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
	Optional<Vehicle> findByRegistration(String registration);
}
