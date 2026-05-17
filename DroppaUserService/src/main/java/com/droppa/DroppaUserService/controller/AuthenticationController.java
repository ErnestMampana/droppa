package com.droppa.DroppaUserService.controller;

import com.droppa.DroppaUserService.service.AuthenticationService;
import com.droppa.DroppaUserService.auth.AuthenticationResponse;
import com.droppa.DroppaUserService.exception.ClientException;
import com.droppa.DroppaUserService.dto.AuthenticationRequest;
import com.droppa.DroppaUserService.dto.CredentialsDTO;
import com.droppa.DroppaUserService.dto.PersonDTO;
import com.droppa.DroppaUserService.dto.RegisterRequest;
import com.droppa.DroppaUserService.dto.UserResponseDTO;
import com.droppa.DroppaUserService.entity.UserAccount;

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
