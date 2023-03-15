/**
 * 
 */
package com.droppa.clone.droppa.controllers;

import java.util.List;

import com.droppa.clone.droppa.dto.VehicleDTO;
import com.droppa.clone.droppa.models.Vehicle;
import com.droppa.clone.droppa.services.VehicleService;
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
@RequestMapping("/vehicle")
@RequiredArgsConstructor
public class VehicleController {

	private final VehicleService vehicleService;

	@PostMapping("/registervehicle")
	public ResponseEntity<Vehicle> registerVegicle(@RequestBody VehicleDTO vehicleDto) {
		Vehicle vehicle =  vehicleService.registerVehicle(vehicleDto);
		return new ResponseEntity<Vehicle>(vehicle,HttpStatus.OK);
	}
	
	@GetMapping("/viewallvehicles")
	public List<Vehicle> viewAllVehicles() {
		List<Vehicle> vehicles = vehicleService.getAllVehicles();
		return vehicles;
	}
	
	@GetMapping("/getvehicle/{registration}")
	public ResponseEntity<Vehicle> getVehicleByRegistration(@PathVariable("registration") String registration){
		Vehicle vehicle = vehicleService.getVehicleByRegistration(registration);
		return new ResponseEntity<Vehicle>(vehicle,HttpStatus.OK);
	}
}
