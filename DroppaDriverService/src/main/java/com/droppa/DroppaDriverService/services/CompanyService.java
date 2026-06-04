/**
 * 
 */
package com.droppa.DroppaDriverService.services;

import java.util.List;

import com.droppa.DroppaDriverService.interfaces.UserServiceClient;

import com.droppa.DroppaDriverService.exception.ClientException;
import com.droppa.DroppaDriverService.dto.CompanyDTO;
import com.droppa.DroppaDriverService.dto.PersonClient;
import com.droppa.DroppaDriverService.entity.Company;
import com.droppa.DroppaDriverService.repositories.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import lombok.RequiredArgsConstructor;

/**
 * @author Ernest Mampana
 *
 */

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {

	private final UserServiceClient userClient;
	private final PartyService partyService;
	private final CompanyRepository companyRepository;

	public Company createCompany(CompanyDTO companyDto) {
		PersonClient userAccount = userClient.getUserByEmail(companyDto.getOwnerId());

		Company company = Company.create(
				generateUniqueCompanyId(),
				companyDto.getCompanyName(),
				userAccount.getId(),
				companyDto.getLocation()
		);

		return companyRepository.save(company);
	}

	@Transactional(readOnly = true)
	public Company getCompanyByCompanyId(String companyId) {
		return companyRepository.findByCompanyId(companyId)
				.orElseThrow(() -> new ClientException("Company not found"));
	}

	@Transactional(readOnly = true)
	public List<Company> viewAllCompanies() {
		return companyRepository.findAll();
	}

	private String generateUniqueCompanyId() {
		String companyId;

		do {
			companyId = partyService.randomChars(10);
		} while (companyRepository.existsByCompanyId(companyId));

		return companyId;
	}
}
