package com.droppa.clone.droppa.controllers;

import java.util.List;

import com.droppa.clone.droppa.dto.DriverDTO;
import com.droppa.clone.droppa.models.DriverAccount;
import com.droppa.clone.droppa.services.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/driver")
@RequiredArgsConstructor
public class DriverController {

	private final DriverService driverService;

	@GetMapping("/viewalldrivers")
	public List<DriverAccount> getAllUsers() {

		return driverService.getAllDrivers();
	}

	@PostMapping("/createdriver")
	public ResponseEntity<DriverAccount> createUser(@RequestBody DriverDTO driver) {
		DriverAccount driverAcc = driverService.createDriverAccount(driver);
		return new ResponseEntity<DriverAccount>(driverAcc,HttpStatus.OK);
	}
	
	@GetMapping("/getdriverbyid/{email}")
	public ResponseEntity<DriverAccount> getDriverById(@PathVariable("email") String email) {
		DriverAccount driverAcc = driverService.getDriverByEmail(email);
		return new ResponseEntity<DriverAccount>(driverAcc,HttpStatus.OK);
	}
}
