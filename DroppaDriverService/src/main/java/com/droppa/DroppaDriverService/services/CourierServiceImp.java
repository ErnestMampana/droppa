/**
 * 
 */
package com.droppa.DroppaDriverService.services;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;


import com.droppa.DroppaDriverService.interfaces.CourierService;
import com.droppa.DroppaDriverService.entity.Courier;
import com.droppa.DroppaDriverService.entity.Parcel;
import com.droppa.DroppaDriverService.repositories.CourierRepository;
import com.droppa.DroppaDriverService.repositories.ParcelRepository;

import lombok.RequiredArgsConstructor;

/**
 * @author Ernest Mampana
 *
 */
@Service
@RequiredArgsConstructor
public class CourierServiceImp implements CourierService {
//	ModelMapper modelMapper = new ModelMapper();
//	private final CourierRepository courierRepository;
//	private final UserService userService;
//	private final ParcelRepository parcelRepository;
//
//	@Override
//	public Courier createCourierBooking(Courier courier) {
//		Courier cou = modelMapper.map(courier, Courier.class);
//		UserAccount person = userService.getUserByEmail("ernest@gmail.com");
////		Courier cou = modelMapper.map(courier, Courier.class);
////		cou.setPerson(person);
////		courierRepository.save(cou);
//		return cou;
//	}

}
