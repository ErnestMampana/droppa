/**
 * 
 */
package com.droppa.DroppaDriverService.repositories;

import java.util.List;
import java.util.Optional;

import com.droppa.DroppaDriverService.entity.DriverAccount;
import com.droppa.DroppaDriverService.enums.AccountStatus;
import com.droppa.DroppaDriverService.enums.DriverAvailability;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


/**
 * @author Ernest Mampana
 *
 */
public interface DriverAccountRepository extends JpaRepository<DriverAccount, Integer> {
	Optional<DriverAccount> findByEmail(String email);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select d from DriverAccount d where lower(d.email) = lower(:email)")
	Optional<DriverAccount> findByEmailForUpdate(@Param("email") String email);

	boolean existsByEmail(String email);

	@Query("""
			select d from DriverAccount d
			where d.status = :status
			and d.availabilityStatus = :availabilityStatus
			and d.isConfirmed = true
			""")
	List<DriverAccount> findConfirmedDriversByStatusAndAvailability(
			@Param("status") AccountStatus status,
			@Param("availabilityStatus") DriverAvailability availabilityStatus);
}
