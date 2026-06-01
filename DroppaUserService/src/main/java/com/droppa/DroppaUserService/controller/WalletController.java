package com.droppa.DroppaUserService.controller;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.droppa.DroppaUserService.dto.LoadWalletRequest;
import com.droppa.DroppaUserService.service.WalletService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {
	
	private final WalletService walletService;
	
	@PutMapping("/loadwallet")
	public ResponseEntity<BigDecimal> loadWallet(@RequestBody LoadWalletRequest request) {
		return new ResponseEntity<BigDecimal>(walletService.loadWallet(request),HttpStatus.OK);
	}

}
