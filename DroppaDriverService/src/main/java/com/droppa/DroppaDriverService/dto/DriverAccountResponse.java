package com.droppa.DroppaDriverService.dto;

import com.droppa.DroppaDriverService.entity.DriverAccount;
import com.droppa.DroppaDriverService.entity.Vehicle;
import com.droppa.DroppaDriverService.entity.VehicleDriver;
import com.droppa.DroppaDriverService.enums.AccountStatus;

public record DriverAccountResponse(
		int id,
		String email,
		boolean confirmed,
		AccountStatus status,
		String vehicleRegistration,
		String driverName,
		String driverSurname,
		long cellphone
) {
	public static DriverAccountResponse from(DriverAccount account) {
		Vehicle vehicle = account.getVehicle();
		VehicleDriver driver = account.getDriver();

		return new DriverAccountResponse(
				account.getId(),
				account.getEmail(),
				account.isConfirmed(),
				account.getStatus(),
				vehicle != null ? vehicle.getRegistration() : null,
				driver != null ? driver.getName() : null,
				driver != null ? driver.getSurname() : null,
				driver != null ? driver.getCelphone() : 0
		);
	}
}
