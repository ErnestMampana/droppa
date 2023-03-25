package com.droppa.clone.droppa.controllers;

import com.droppa.clone.droppa.auth.AuthenticationResponse;
import com.droppa.clone.droppa.common.ClientException;
import com.droppa.clone.droppa.dto.AuthenticationRequest;
import com.droppa.clone.droppa.dto.CredentialsDTO;
import com.droppa.clone.droppa.dto.OtpDTO;
import com.droppa.clone.droppa.dto.PersonDTO;
import com.droppa.clone.droppa.dto.RegisterRequest;
import com.droppa.clone.droppa.dto.UserResponseDTO;
import com.droppa.clone.droppa.models.UserAccount;
import com.droppa.clone.droppa.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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

}
