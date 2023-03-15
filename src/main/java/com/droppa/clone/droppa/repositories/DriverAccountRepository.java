/**
 * 
 */
package com.droppa.clone.droppa.repositories;

import java.util.Optional;

import com.droppa.clone.droppa.models.DriverAccount;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * @author Ernest Mampana
 *
 */
public interface DriverAccountRepository extends JpaRepository<DriverAccount, Integer> {
	Optional<DriverAccount> findByEmail(String email);
}
