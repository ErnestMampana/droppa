/**
 * 
 */
package com.droppa.clone.droppa.repositories;

import java.util.Optional;

import com.droppa.clone.droppa.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * @author Ernest Mampana
 *
 */
public interface CompanyRepository extends JpaRepository<Company, Integer> {
	Optional<Company> findByCompanyId(String companyId);
}
