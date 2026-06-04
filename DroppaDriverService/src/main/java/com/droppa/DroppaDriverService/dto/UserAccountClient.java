package com.droppa.DroppaDriverService.dto;

public record UserAccountClient(
		int id,
		String email,
		boolean confirmed,
		String status
) {
	public boolean isActiveAndConfirmed() {
		return confirmed && "ACTIVE".equals(status);
	}
}
