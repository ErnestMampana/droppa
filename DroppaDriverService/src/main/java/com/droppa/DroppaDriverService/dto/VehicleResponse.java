package com.droppa.DroppaDriverService.dto;

import com.droppa.DroppaDriverService.entity.Company;
import com.droppa.DroppaDriverService.entity.DriverAccount;
import com.droppa.DroppaDriverService.entity.Vehicle;

import java.time.LocalDate;

public record VehicleResponse(
		int id,
		String registration,
		String make,
		String type,
		LocalDate discExpiryDate,
		String companyId,
		String companyName,
		String assignedDriverEmail
) {
	public static VehicleResponse from(Vehicle vehicle) {
		Company company = vehicle.getCompany();
		DriverAccount driverAccount = vehicle.getDriverAccount();

		return new VehicleResponse(
				vehicle.getId(),
				vehicle.getRegistration(),
				vehicle.getMake(),
				vehicle.getType(),
				vehicle.getDiscExpiryDate(),
				company != null ? company.getCompanyId() : null,
				company != null ? company.getCompanyName() : null,
				driverAccount != null ? driverAccount.getEmail() : null
		);
	}
}
