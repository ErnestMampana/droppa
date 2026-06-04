/**
 * 
 */
package com.droppa.DroppaDriverService.services;

import java.util.List;

import com.droppa.DroppaDriverService.exception.ClientException;
import com.droppa.DroppaDriverService.dto.VehicleDTO;
import com.droppa.DroppaDriverService.entity.Company;
import com.droppa.DroppaDriverService.entity.Vehicle;
import com.droppa.DroppaDriverService.repositories.VehicleRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

/**
 * @author Ernest Mampana
 *
 */
@Service
@RequiredArgsConstructor
@Transactional
public class VehicleService {

	private final VehicleRepository vehicleRepo;
	private final CompanyService companyService;

	@Transactional(readOnly = true)
	public Vehicle getVehicleByRegistration(String vehicleReg) {
		return vehicleRepo.findByRegistration(vehicleReg)
				.orElseThrow(() -> new ClientException("Vehicle not found"));
	}

	public Vehicle registerVehicle(VehicleDTO vehicleDto) {
		if (vehicleRepo.existsByRegistration(vehicleDto.getRegistration())) {
			throw new ClientException("This vehicle is already registered.");
		}

		Company company = companyService.getCompanyByCompanyId(vehicleDto.getCompanyId());
		Vehicle vehicle = Vehicle.register(
				vehicleDto.getRegistration(),
				vehicleDto.getMake(),
				vehicleDto.getType(),
				vehicleDto.getDiscExpiryDate(),
				company
		);

		return vehicleRepo.save(vehicle);
	}

	@Transactional(readOnly = true)
	public List<Vehicle> getAllVehicles() {
		return vehicleRepo.findAll();
	}

}
