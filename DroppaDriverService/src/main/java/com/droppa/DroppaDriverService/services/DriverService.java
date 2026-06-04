package com.droppa.DroppaDriverService.services;

import java.util.List;

import com.droppa.DroppaDriverService.exception.ClientException;
import com.droppa.DroppaDriverService.dto.DriverDTO;
import com.droppa.DroppaDriverService.dto.PersonClient;
import com.droppa.DroppaDriverService.dto.UserAccountClient;
import com.droppa.DroppaDriverService.entity.DriverAccount;
import com.droppa.DroppaDriverService.entity.Vehicle;
import com.droppa.DroppaDriverService.entity.VehicleDriver;
import com.droppa.DroppaDriverService.interfaces.UserServiceClient;
import com.droppa.DroppaDriverService.repositories.DriverAccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DriverService {

	private final DriverAccountRepository driverAccountRepository;
	private final VehicleService vehicleService;
	private final UserServiceClient userServiceClient;

	@Transactional(readOnly = true)
	public List<DriverAccount> getAllDrivers() {
		return driverAccountRepository.findAll();
	}

	public DriverAccount createDriverAccount(DriverDTO driverDto, String driverEmail) {
		log.debug("Creating new driver");
		String email = requireEmail(driverEmail);
		UserAccountClient userAccount = userServiceClient.getUserAccountByEmail(email);

		log.debug("Creating driver account for user account");
		if (!userAccount.isActiveAndConfirmed()) {
			throw new ClientException("Only confirmed active users can register as drivers");
		}

		if (driverAccountRepository.existsByEmail(email)) {
			throw new ClientException("Driver with Email '" + email + "' already exist");
		}

		Vehicle vehicleData = vehicleService.getVehicleByRegistration(driverDto.getRegistration());
		PersonClient person = requirePerson(userServiceClient.getUserByEmail(email));
		VehicleDriver driver = VehicleDriver.create(
				email,
				requireText(person.getUserName(), "userName"),
				requireText(person.getSurname(), "surname"),
				requireCellphone(person.getCellphone())
		);

		DriverAccount driverAccount = DriverAccount.register(
				email,
				vehicleData,
				driver
		);

		return driverAccountRepository.save(driverAccount);
	}

	@Transactional(readOnly = true)
	public DriverAccount getDriverByEmail(String email) {
		return driverAccountRepository.findByEmail(email)
				.orElseThrow(() -> new ClientException("Driver not found"));
	}

	private String requireEmail(String email) {
		if (email == null || email.isBlank()) {
			throw new ClientException("Authenticated driver email is required");
		}

		return email.trim();
	}

	private PersonClient requirePerson(PersonClient person) {
		if (person == null) {
			throw new ClientException("User profile not found");
		}

		return person;
	}

	private String requireText(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			throw new ClientException("User profile " + fieldName + " is required");
		}

		return value.trim();
	}

	private long requireCellphone(String cellphone) {
		String value = requireText(cellphone, "cellphone")
				.replace(" ", "")
				.replace("-", "");

		if (value.startsWith("+")) {
			value = value.substring(1);
		}

		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			throw new ClientException("User profile cellphone must be numeric");
		}
	}

}
