package com.droppa.DroppaUserService.dto;

import com.droppa.DroppaUserService.entity.UserAccount;
import com.droppa.DroppaUserService.enums.AccountStatus;

public record UserAccountStatusResponse(
		int id,
		String email,
		boolean confirmed,
		AccountStatus status
) {
	public static UserAccountStatusResponse from(UserAccount account) {
		return new UserAccountStatusResponse(
				account.getId(),
				account.getEmail(),
				account.isConfirmed(),
				account.getStatus()
		);
	}
}
