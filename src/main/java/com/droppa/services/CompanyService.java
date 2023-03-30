/**
 * 
 */
package com.droppa.clone.droppa.services;

import java.util.List;
import java.util.Optional;

import com.droppa.clone.droppa.common.ClientException;
import com.droppa.clone.droppa.dto.CompanyDTO;
import com.droppa.clone.droppa.enums.AccountStatus;
import com.droppa.clone.droppa.models.Company;
import com.droppa.clone.droppa.models.Person;
import com.droppa.clone.droppa.models.UserAccount;
import com.droppa.clone.droppa.repositories.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

/**
 * @author Ernest Mampana
 *
 */

@Service
@RequiredArgsConstructor
public class CompanyService {

	private UserService userService;

	private PartyService partyService;

	private CompanyRepository companyRepository;

	public Company createCompany(CompanyDTO companyDto) {
		UserAccount userAccount = userService.getUserByEmail(companyDto.getOwnerId());
		if (userAccount.getStatus().equals(AccountStatus.ACTIVE)) {
			Person person = userAccount.getPerson();
			var company = Company.builder()
					.companyId(partyService.randomChars(10))
					.companyName(companyDto.getCompanyName())
					.owner(person)
					.location(companyDto.getLocation()).build();

			companyRepository.save(company);

			return company;
		} else {
			if (userAccount.getStatus().equals(AccountStatus.AWAITING_CONFIRMATION)) {
				throw new ClientException("Please confirm your account first.");
			} else if (userAccount.getStatus().equals(AccountStatus.AWAITING_PWD_RESET)) {
				throw new ClientException("You haven't set confirmed your new password");
			} else {
				throw new ClientException(
						"Your account has been suspended please contact Droppa Clone for re-activation.");
			}
		}

	}

	public Company getCompanyByCompanyId(String companyId) {
		Optional<Company> optionalCompany = companyRepository.findByCompanyId(companyId);
		if (optionalCompany.isPresent()) {
			return optionalCompany.get();
		} else {
			throw new ClientException("Company not found");
		}
	}

	public List<Company> viewAllCompanies() {
		return companyRepository.findAll();
	}
}
