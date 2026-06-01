package com.droppa.DroppaUserService.exception;

public class UserNotFoundException extends RuntimeException{
	
	public UserNotFoundException(String email) {
		super("Account not found for email: " + email);
	}

}
