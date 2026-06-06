package com.droppa.DroppaDriverService.interfaces;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.droppa.DroppaDriverService.dto.PersonClient;
import com.droppa.DroppaDriverService.dto.UserAccountClient;

@FeignClient(name = "user-service", url = "${services.user.url}")
public interface UserServiceClient {
	
	@GetMapping("/api/v1/user/getuserbyemail/{email}")
	PersonClient getUserByEmail(@PathVariable("email") String email);

	@GetMapping("/api/v1/user/accounts/{email}")
	UserAccountClient getUserAccountByEmail(@PathVariable("email") String email);

}
