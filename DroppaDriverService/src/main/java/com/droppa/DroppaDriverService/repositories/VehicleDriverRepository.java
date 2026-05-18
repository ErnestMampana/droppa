/**
 * 
 */
package com.droppa.DroppaDriverService.repositories;

import java.util.Optional;

import com.droppa.DroppaDriverService.entity.VehicleDriver;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Ernest Mampana
 *
 */
public interface VehicleDriverRepository extends JpaRepository<VehicleDriver, Integer> {

	Optional<VehicleDriver> findByEmail(String email);

}
