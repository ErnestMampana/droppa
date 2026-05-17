package com.droppa.services;

import java.util.List;
import java.util.Optional;

import com.droppa.DroppaUserService.enums.AccountStatus;
import com.droppa.common.ClientException;
import com.droppa.dto.DriverDTO;
import com.droppa.models.DriverAccount;
import com.droppa.models.Vehicle;
import com.droppa.models.VehicleDriver;
import com.droppa.repositories.DriverAccountRepository;
import com.droppa.repositories.VehicleDriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DriverService {

	private DriverAccountRepository driverAccRepo;

	private DriverAccountRepository driverAccountRepository;

	private VehicleDriverRepository vehicleDriverRepository;

	private VehicleService vehicleService;

	public List<DriverAccount> getAllDrivers() {
		return driverAccRepo.findAll();
	}

	public DriverAccount createDriverAccount(DriverDTO driverDto) {

		Optional<DriverAccount> driverOptional = driverAccRepo.findByEmail(driverDto.getEmail());

		if (driverOptional.isPresent())
			throw new ClientException("Driver with Email '" + driverOptional.get().getEmail() + "' already exist");

		var driver = VehicleDriver.builder()
				.email(driverDto.getEmail())
				.name(driverDto.getName())
				.surname(driverDto.getSurname())
				.celphone(driverDto.getCellphone())
				.driverLicence(null).build();


		Vehicle vehicleData = vehicleService.getVehicleByRegistration(driverDto.getRegistration());

		var driverAcc = DriverAccount.builder()
				.email(driverDto.getEmail())
				.password(driverDto.getPassword())
				.isConfirmed(false)
				.vehicle(vehicleData)
				.driver(driver)
				.status(AccountStatus.AWAITING_CONFIRMATION).build();

		vehicleDriverRepository.save(driver);
		driverAccountRepository.save(driverAcc);

		return driverAcc;
	}

	public DriverAccount getDriverByEmail(String email) {
		Optional<DriverAccount> driverAccountOptional = driverAccountRepository.findByEmail(email);

		if (driverAccountOptional.isPresent()) {
			return driverAccountOptional.get();
		} else {
			throw new ClientException("Driver not found");
		}
	}

}
