package com.droppa.DroppaDriverService.dto;

import com.droppa.DroppaDriverService.entity.Company;

public record CompanyResponse(
		int id,
		String companyId,
		String companyName,
		int ownerId,
		String location
) {
	public static CompanyResponse from(Company company) {
		return new CompanyResponse(
				company.getId(),
				company.getCompanyId(),
				company.getCompanyName(),
				company.getOwnerId(),
				company.getLocation()
		);
	}
}
