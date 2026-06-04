package com.droppa.DroppaUserService.exception;

@SuppressWarnings("serial")
public class OtpExpiredException extends ClientException {

	public OtpExpiredException() {
		super("OTP has expired");
	}
}
