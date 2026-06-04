package com.droppa.DroppaDriverService.controllers;

import java.util.List;

import com.droppa.DroppaDriverService.dto.DriverAccountResponse;
import com.droppa.DroppaDriverService.dto.DriverDTO;
import com.droppa.DroppaDriverService.entity.DriverAccount;
import com.droppa.DroppaDriverService.services.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/drivers")
@RequiredArgsConstructor
@Slf4j
public class DriverController {

	private final DriverService driverService;

	@GetMapping("/viewalldrivers")
	public List<DriverAccountResponse> getAllUsers() {

		return driverService.getAllDrivers()
				.stream()
				.map(DriverAccountResponse::from)
				.toList();
	}

	@PostMapping("/createdriver")
	public ResponseEntity<DriverAccountResponse> createUser(
			@RequestBody DriverDTO driver,
			@RequestHeader("X-User-Email") String driverEmail) {
		log.debug("Trying to create a driver");
		DriverAccount driverAcc = driverService.createDriverAccount(driver, driverEmail);

		return new ResponseEntity<>(DriverAccountResponse.from(driverAcc), HttpStatus.CREATED);
	}
	
	@GetMapping("/getdriverbyid/{email}")
	public ResponseEntity<DriverAccountResponse> getDriverById(@PathVariable("email") String email) {
		DriverAccount driverAcc = driverService.getDriverByEmail(email);
		return ResponseEntity.ok(DriverAccountResponse.from(driverAcc));
	}
}
