/**
 * 
 */
package com.droppa.services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.droppa.DroppaUserService.entity.Person;
import com.droppa.DroppaUserService.entity.UserAccount;
import com.droppa.DroppaUserService.service.UserService;
import com.droppa.interfaces.CourierService;
import com.droppa.models.Courier;
import com.droppa.models.Parcel;
import com.droppa.repositories.CourierRepository;
import com.droppa.repositories.ParcelRepository;

import lombok.RequiredArgsConstructor;

/**
 * @author Ernest Mampana
 *
 */
@Service
@RequiredArgsConstructor
public class CourierServiceImp implements CourierService {
	ModelMapper modelMapper = new ModelMapper();
	private final CourierRepository courierRepository;
	private final UserService userService;
	private final ParcelRepository parcelRepository;

	@Override
	public Courier createCourierBooking(Courier courier) {
		Courier cou = modelMapper.map(courier, Courier.class);
		UserAccount person = userService.getUserByEmail("ernest@gmail.com");
//		Courier cou = modelMapper.map(courier, Courier.class);
//		cou.setPerson(person);
//		courierRepository.save(cou);
		return cou;
	}

}
