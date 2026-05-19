/**
 * 
 */
package com.droppa.DroppaDriverService.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.droppa.DroppaDriverService.entity.Courier;
import com.droppa.DroppaDriverService.services.CourierServiceImp;

import lombok.RequiredArgsConstructor;

/**
 * @author Ernest Mampana
 *
 */
@RestController
@RequestMapping("/api/v1/courier")
@RequiredArgsConstructor
public class CourierController {
	
	private final CourierServiceImp courierServiceImp;
	
//	@PostMapping("/createBooking")
//	public ResponseEntity<Courier> createCourierBooking(@RequestBody Courier cou){
//		return ResponseEntity.ok(courierServiceImp.createCourierBooking(cou));
//	}

}


   
   
   
   
   
   
   



