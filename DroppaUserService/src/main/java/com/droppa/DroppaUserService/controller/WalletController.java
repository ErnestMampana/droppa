package com.droppa.DroppaUserService.controller;

import java.math.BigDecimal;

import com.droppa.DroppaUserService.exception.ClientException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.droppa.DroppaUserService.dto.LoadWalletRequest;
import com.droppa.DroppaUserService.service.WalletService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {
	
	private final WalletService walletService;
	private final ObjectMapper objectMapper;

	@PutMapping(
			value = "/loadwallet",
			consumes = MediaType.APPLICATION_JSON_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<BigDecimal> loadWallet(@Valid @RequestBody LoadWalletRequest request,
												 Authentication authentication) {
		return loadWallet(request, authenticatedEmail(authentication));
	}

	@PutMapping(
			value = "/loadwallet",
			consumes = MediaType.TEXT_PLAIN_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE
	)
	public ResponseEntity<BigDecimal> loadWalletFromText(@RequestBody String requestBody,
														 Authentication authentication) {
		LoadWalletRequest request = LoadWalletRequest.builder()
				.amount(readAmount(requestBody))
				.build();

		return loadWallet(request, authenticatedEmail(authentication));
	}

	private ResponseEntity<BigDecimal> loadWallet(LoadWalletRequest request, String email) {
		return new ResponseEntity<BigDecimal>(walletService.loadWallet(request, email), HttpStatus.OK);
	}

	private String authenticatedEmail(Authentication authentication) {
		if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
			throw new ClientException("Authenticated user email is required");
		}

		return authentication.getName();
	}

	private BigDecimal readAmount(String requestBody) {
		if (requestBody == null || requestBody.isBlank()) {
			throw new ClientException("Wallet amount is required");
		}

		String trimmedBody = requestBody.trim();

		try {
			if (trimmedBody.startsWith("{")) {
				return objectMapper.readValue(trimmedBody, LoadWalletRequest.class)
						.getAmount();
			}

			return new BigDecimal(trimmedBody);
		} catch (JsonProcessingException | NumberFormatException e) {
			throw new ClientException("Invalid wallet amount");
		}
	}

}
