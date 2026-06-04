package com.droppa.DroppaUserService.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.droppa.DroppaUserService.dto.LoadWalletRequest;
import com.droppa.DroppaUserService.entity.UserAccount;
import com.droppa.DroppaUserService.exception.ClientException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletService {
	
	private final UserService userService;
	
	@Transactional
	public BigDecimal loadWallet(LoadWalletRequest request,String email) {
		if (request == null || request.getAmount() == null) {
			throw new ClientException("Wallet amount is required");
		}

		if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
			throw new ClientException("Wallet amount must be positive");
		}

		UserAccount userAccount = userService.getUserByEmail(email);
		userAccount.loadWallet(request.getAmount());

		return userAccount.getPerson().getWalletBalance();

	}

}
