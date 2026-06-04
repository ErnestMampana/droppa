package com.droppa.DroppaBookingService.exceptions;

public class UserNotFoundException extends RuntimeException{
	
	public UserNotFoundException(String email) {
		super("Account not found for email: " + email);
	}

}
