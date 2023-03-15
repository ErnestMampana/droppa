package com.droppa.clone.droppa.controllers;

import java.util.List;

import com.droppa.clone.droppa.models.Person;
import com.droppa.clone.droppa.models.UserAccount;
import com.droppa.clone.droppa.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {


	private final UserService userService;

	@GetMapping("/viewallusers")
	public List<UserAccount> getAllUsers() {
		return userService.getAllUsers();
	}

//	@PUT
//	@Path("/mobile/confirmation/{mobile}/{code}")
//	public Response confirmMobile(@PathParam("code") int code, @PathParam("mobile") String mobile) {
//		String resp = userService.confirmMobile(mobile, code);
//		return Response.ok(resp).build();
//	}
//
	@PutMapping("/email/confirmation/{email}")
	public ResponseEntity<String> confirmEmail(@PathVariable("email") String email,
			@RequestParam(required = true) int code) {
		String resp = userService.confirmEmail(email, code);
		return new ResponseEntity<String>(resp, HttpStatus.OK);
	}

	@GetMapping("/getuserbyemail/{email}")
	public ResponseEntity<Person> getUserByEmail(@PathVariable("email") String email) {
		Person account = userService.getUserByEmail(email).getPerson();
		return new ResponseEntity<Person>(account, HttpStatus.OK);
	}


	@GetMapping("/requestPasswordReset/{email}")
	public ResponseEntity<Integer> requestPasswordReset(@PathVariable("email") String email) {
		int otp = userService.requestPasswordReset(email);
		return new ResponseEntity<Integer>(otp, HttpStatus.OK);
	}

	@PutMapping("/resetPassword/{username}")
	public ResponseEntity<UserAccount> resetPassword(@PathVariable("username") String username,
			@RequestParam(required = true) int otp, @RequestParam(required = true) String password) {
		UserAccount userAcc = userService.resetPassword(otp, username, password);
		return new ResponseEntity<UserAccount>(userAcc, HttpStatus.OK);
	}

	@PutMapping("/loadwallet/{username}")
	public ResponseEntity<UserAccount> loadWallet(@PathVariable("username") String username,
			@RequestParam(required = true) double amount) {
		UserAccount userAccount = userService.loadWallet(username, amount);
		return new ResponseEntity<UserAccount>(userAccount,HttpStatus.OK);
	}

}
