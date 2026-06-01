package com.droppa.DroppaUserService.testdata;

import com.droppa.DroppaUserService.dto.ConfirmEmailRequest;
import com.droppa.DroppaUserService.dto.CredentialsDTO;
import com.droppa.DroppaUserService.dto.PasswordResetRequest;
import com.droppa.DroppaUserService.dto.PersonDTO;
import com.droppa.DroppaUserService.dto.ResetPasswordRequest;

public class RequestMother {
	
	 public static PersonDTO validPerson() {
	        PersonDTO dto = new PersonDTO();
	        dto.setEmail("ernest@gmail.com");
	        dto.setPassword("password123");
	        dto.setSurname("Ernest");
	        dto.setSurname("Mohlala");
	        dto.setCellphone("0723568069");
	        return dto;
	    }

	    public static CredentialsDTO validCredentials() {
	        CredentialsDTO dto = new CredentialsDTO();
	        dto.setUsername("ernest@gmail.com");
	        dto.setPassword("password123");
	        return dto;
	    }

	    public static ConfirmEmailRequest confirmEmail() {
	        ConfirmEmailRequest dto = new ConfirmEmailRequest();
	        dto.setEmail("ernest@gmail.com");
	        dto.setCode("12345");
	        return dto;
	    }

	    public static PasswordResetRequest passwordReset() {
	        PasswordResetRequest dto = new PasswordResetRequest();
	        dto.setEmail("ernest@gmail.com");
	        return dto;
	    }

	    public static ResetPasswordRequest resetPassword() {
	        ResetPasswordRequest dto = new ResetPasswordRequest();
	        dto.setUsername("ernest@gmail.com");
	        dto.setPassword("00000000");
	        dto.setOtp("12345");
	        return dto;
	    }

}
