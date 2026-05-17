/**
 * 
 */
package com.droppa.repositories;

import java.util.Optional;

import com.droppa.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * @author Ernest Mampana
 *
 */
public interface CompanyRepository extends JpaRepository<Company, Integer> {
	Optional<Company> findByCompanyId(String companyId);
}
