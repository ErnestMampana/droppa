/**
 * 
 */
package com.droppa.DroppaDriverService.services;

import java.util.List;
import java.util.Optional;

import com.droppa.DroppaDriverService.interfaces.UserServiceClient;
//import com.droppa.DroppaUserService.entity.Person;

import com.droppa.DroppaDriverService.exception.ClientException;
import com.droppa.DroppaDriverService.dto.CompanyDTO;
import com.droppa.DroppaDriverService.dto.PersonClient;
import com.droppa.DroppaDriverService.entity.Company;
import com.droppa.DroppaDriverService.repositories.CompanyRepository;
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

	private final UserServiceClient userClient;

	private PartyService partyService;

	private CompanyRepository companyRepository;

	public Company createCompany(CompanyDTO companyDto) {
		PersonClient userAccount = userClient.getUserByEmail(companyDto.getOwnerId());
		//only return active account from user service
//		if (userAccount.getStatus().equals(AccountStatus.ACTIVE)) {
		//	Person person = userAccount.getPerson();
			var company = Company.builder()
					.companyId(partyService.randomChars(10))
					.companyName(companyDto.getCompanyName())
					.ownerId(userAccount.getId())
					.location(companyDto.getLocation()).build();

			companyRepository.save(company);

			return company;
		} 
	
//	else {
//			if (userAccount.getStatus().equals(AccountStatus.AWAITING_CONFIRMATION)) {
//				throw new ClientException("Please confirm your account first.");
//			} else if (userAccount.getStatus().equals(AccountStatus.AWAITING_PWD_RESET)) {
//				throw new ClientException("You haven't set confirmed your new password");
//			} else {
//				throw new ClientException(
//						"Your account has been suspended please contact Droppa Clone for re-activation.");
//			}
//		}

	

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
