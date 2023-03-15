/**
 * 
 */
package com.droppa.clone.droppa.controllers;

import java.util.List;

import com.droppa.clone.droppa.dto.CompanyDTO;
import com.droppa.clone.droppa.models.Company;
import com.droppa.clone.droppa.services.CompanyService;
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
	public ResponseEntity<Company> createCompany(@RequestBody CompanyDTO companyDTO) {
		Company company = companyService.createCompany(companyDTO);
		return new ResponseEntity<Company>(company,HttpStatus.OK);
	}
	
	@GetMapping("/getcompany/{id}")
	public ResponseEntity<Company> getCompanyById(@PathVariable("id") String id) {
		Company company = companyService.getCompanyByCompanyId(id);
		return new ResponseEntity<Company>(company,HttpStatus.OK);
	}
	
	@GetMapping("/viewallcompanies")
	public List<Company> viewAllCompanies() {
		return companyService.viewAllCompanies();
	}

}
