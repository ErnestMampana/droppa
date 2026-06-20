package com.droppa.DroppaUserService.exception;

public class IncorrectPasswordException extends RuntimeException {

	public IncorrectPasswordException() {
		super("Incorrect password");
	}
}
