package com.droppa.DroppaUserService.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.droppa.DroppaUserService.dto.LoadWalletRequest;
import com.droppa.DroppaUserService.entity.UserAccount;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WalletService {
	
	private final UserService userService;
	
	@Transactional
	public BigDecimal loadWallet(LoadWalletRequest request) {

		UserAccount userAccount = userService.getUserByEmail(request.getUsername());
		userAccount.loadWallet(request.getAmount());

		return userAccount.getPerson().getWalletBalance();

	}

}
