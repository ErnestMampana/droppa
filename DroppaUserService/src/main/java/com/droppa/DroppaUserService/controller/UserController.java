package com.droppa.DroppaUserService.controller;

import java.math.BigDecimal;
import java.util.List;

import com.droppa.DroppaUserService.dto.UserAccountStatusResponse;
import com.droppa.DroppaUserService.dto.UserResponseDTO;
import com.droppa.DroppaUserService.entity.Person;
import com.droppa.DroppaUserService.entity.UserAccount;
import com.droppa.DroppaUserService.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {


	private final UserService userService;


	@GetMapping("/getuserbyemail/{email}")
	public ResponseEntity<Person> getUserByEmail(@PathVariable("email") String email) {
		return new ResponseEntity<Person>(userService.getUserByEmail(email).getPerson(), HttpStatus.OK);
	}

	@GetMapping("/accounts/{email}")
	public ResponseEntity<UserAccountStatusResponse> getUserAccountByEmail(@PathVariable("email") String email) {
		return ResponseEntity.ok(UserAccountStatusResponse.from(userService.getUserByEmail(email)));
	}


}
