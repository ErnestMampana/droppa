/**
 * 
 */
package com.droppa.clone.droppa.services;

import java.util.List;
import java.util.Optional;

import com.droppa.clone.droppa.common.ClientException;
import com.droppa.clone.droppa.dto.VehicleDTO;
import com.droppa.clone.droppa.models.Company;
import com.droppa.clone.droppa.models.Vehicle;
import com.droppa.clone.droppa.repositories.VehicleRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

/**
 * @author Ernest Mampana
 *
 */
@Service
@RequiredArgsConstructor
public class VehicleService {

	private final VehicleRepository vehicleRepo;

	private CompanyService companyService;

	private ModelMapper modelMapper;

	public Vehicle getVehicleByRegistration(String vehicleReg) {
		Optional<Vehicle> vehicleOptional = vehicleRepo.findByRegistration(vehicleReg);

		if (vehicleOptional.isPresent()) {
			return vehicleOptional.get();
		} else {
			throw new ClientException("Vehicle not found");
		}

	}

	public Vehicle registerVehicle(VehicleDTO vehicleDto) {

		Company company = companyService.getCompanyByCompanyId(vehicleDto.getCompanyId());

		Optional<Vehicle> vehicleOptional = vehicleRepo.findByRegistration(vehicleDto.getRegistration());

		if (vehicleOptional.isPresent()) {
			throw new ClientException("This vehicle is already registered.");
		}

		Vehicle vehicle = modelMapper.map(vehicleDto, Vehicle.class);
		vehicle.setCompany(company);

		vehicleRepo.save(vehicle);

		return vehicle;
	}

	public List<Vehicle> getAllVehicles() {
		return vehicleRepo.findAll();
	}

}
