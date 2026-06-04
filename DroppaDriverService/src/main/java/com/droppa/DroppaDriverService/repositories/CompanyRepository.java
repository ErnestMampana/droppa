/**
 * 
 */
package com.droppa.DroppaDriverService.repositories;

import java.util.Optional;

import com.droppa.DroppaDriverService.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * @author Ernest Mampana
 *
 */
public interface CompanyRepository extends JpaRepository<Company, Integer> {
	Optional<Company> findByCompanyId(String companyId);

	boolean existsByCompanyId(String companyId);
}
