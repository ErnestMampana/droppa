package com.droppa.DroppaBookingService.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.droppa.DroppaBookingService.dto.PersonClient;

@FeignClient(name = "user-service", url = "http://localhost:8081")
public interface UserServiceClient {
	
	@GetMapping("/api/v1/user/getuserbyemail/{email}")
	PersonClient getUserByEmail(@PathVariable("email") String email);

}