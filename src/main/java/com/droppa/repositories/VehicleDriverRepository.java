/**
 * 
 */
package com.droppa.repositories;

import java.util.Optional;

import com.droppa.models.VehicleDriver;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Ernest Mampana
 *
 */
public interface VehicleDriverRepository extends JpaRepository<VehicleDriver, Integer> {

	Optional<VehicleDriver> findByEmail(String email);

}
