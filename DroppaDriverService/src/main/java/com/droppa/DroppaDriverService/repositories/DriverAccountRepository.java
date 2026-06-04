/**
 * 
 */
package com.droppa.DroppaDriverService.repositories;

import java.util.Optional;

import com.droppa.DroppaDriverService.entity.DriverAccount;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * @author Ernest Mampana
 *
 */
public interface DriverAccountRepository extends JpaRepository<DriverAccount, Integer> {
	Optional<DriverAccount> findByEmail(String email);

	boolean existsByEmail(String email);
}
