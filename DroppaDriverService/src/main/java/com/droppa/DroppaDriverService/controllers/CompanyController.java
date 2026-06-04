/**
 * 
 */
package com.droppa.DroppaDriverService.controllers;

import java.util.List;

import com.droppa.DroppaDriverService.dto.CompanyDTO;
import com.droppa.DroppaDriverService.dto.CompanyResponse;
import com.droppa.DroppaDriverService.entity.Company;
import com.droppa.DroppaDriverService.services.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



/**
 * @author Ernest Mampana
 *
 */

@RestController
@RequestMapping("/company")
@RequiredArgsConstructor
public class CompanyController {

	private final CompanyService companyService;
	
	@PostMapping("/createcompany")
	public ResponseEntity<CompanyResponse> createCompany(@RequestBody CompanyDTO companyDTO) {
		Company company = companyService.createCompany(companyDTO);
		return new ResponseEntity<>(CompanyResponse.from(company), HttpStatus.CREATED);
	}
	
	@GetMapping("/getcompany/{id}")
	public ResponseEntity<CompanyResponse> getCompanyById(@PathVariable("id") String id) {
		Company company = companyService.getCompanyByCompanyId(id);
		return ResponseEntity.ok(CompanyResponse.from(company));
	}
	
	@GetMapping("/viewallcompanies")
	public List<CompanyResponse> viewAllCompanies() {
		return companyService.viewAllCompanies()
				.stream()
				.map(CompanyResponse::from)
				.toList();
	}

}
