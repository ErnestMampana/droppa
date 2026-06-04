/**
 * 
 */
package com.droppa.DroppaDriverService.controllers;

import java.util.List;

import com.droppa.DroppaDriverService.dto.VehicleDTO;
import com.droppa.DroppaDriverService.dto.VehicleResponse;
import com.droppa.DroppaDriverService.entity.Vehicle;
import com.droppa.DroppaDriverService.services.VehicleService;
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
	public ResponseEntity<VehicleResponse> registerVegicle(@RequestBody VehicleDTO vehicleDto) {
		Vehicle vehicle =  vehicleService.registerVehicle(vehicleDto);
		return new ResponseEntity<>(VehicleResponse.from(vehicle), HttpStatus.CREATED);
	}
	
	@GetMapping("/viewallvehicles")
	public List<VehicleResponse> viewAllVehicles() {
		return vehicleService.getAllVehicles()
				.stream()
				.map(VehicleResponse::from)
				.toList();
	}
	
	@GetMapping("/getvehicle/{registration}")
	public ResponseEntity<VehicleResponse> getVehicleByRegistration(@PathVariable("registration") String registration){
		Vehicle vehicle = vehicleService.getVehicleByRegistration(registration);
		return ResponseEntity.ok(VehicleResponse.from(vehicle));
	}
}
