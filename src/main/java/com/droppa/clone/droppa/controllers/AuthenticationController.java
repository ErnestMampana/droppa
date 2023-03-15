package com.droppa.clone.droppa.controllers;

import com.droppa.clone.droppa.auth.AuthenticationResponse;
import com.droppa.clone.droppa.dto.AuthenticationRequest;
import com.droppa.clone.droppa.dto.CredentialsDTO;
import com.droppa.clone.droppa.dto.PersonDTO;
import com.droppa.clone.droppa.dto.RegisterRequest;
import com.droppa.clone.droppa.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService service;

	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> register(@RequestBody PersonDTO request) {
		try {
			return ResponseEntity.ok(service.createUserAccount(request));
		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@PostMapping("/login")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody CredentialsDTO request) {
		return ResponseEntity.ok(service.authenticate(request));
	}

}
