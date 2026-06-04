package com.droppa.DroppaUserService.controller;

import com.droppa.DroppaUserService.service.AuthenticationService;

import com.droppa.DroppaUserService.dto.ConfirmEmailRequest;
import com.droppa.DroppaUserService.dto.CredentialsDTO;
import com.droppa.DroppaUserService.dto.PasswordResetRequest;
import com.droppa.DroppaUserService.dto.PersonDTO;
import com.droppa.DroppaUserService.dto.ResetPasswordRequest;
import com.droppa.DroppaUserService.dto.UserResponseDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService service;

	@PostMapping("/register")
	public ResponseEntity<UserResponseDTO> register(@RequestBody PersonDTO request) {
		return ResponseEntity.ok(service.createUserAccount(request));
	}

	@PostMapping("/login")
	public ResponseEntity<UserResponseDTO> authenticate(@RequestBody CredentialsDTO request) {
		return ResponseEntity.ok(service.authenticate(request));
	}
	
	@PostMapping("/confirm-email")
    public ResponseEntity<UserResponseDTO> confirmEmail(@RequestBody ConfirmEmailRequest request) {
        return ResponseEntity.ok(service.confirmEmail(request));
    }

    @PostMapping("/password-reset/request")
    public ResponseEntity<String> requestPasswordReset(@RequestBody PasswordResetRequest request) {
        return ResponseEntity.ok(service.requestPasswordReset(request.getEmail()));
    }
    
    @PutMapping("/reset-password")
	public ResponseEntity<UserResponseDTO> resetPassword(@RequestBody ResetPasswordRequest request) {
    	return ResponseEntity.ok(service.resetPassword(request));
	}

	@PutMapping("/refresh-otp")
	public ResponseEntity<String> refreshOtp(@RequestBody PasswordResetRequest request) {
		return ResponseEntity.ok(service.refreshOtp(request.getEmail()));
	}
    
    

}
