package com.droppa.clone.droppa.enums;

public enum AccountStatus {
	ACTIVE("Active"), 
	SUSPENDED("Suspended"), 
	AWAITING_CONFIRMATION("Awaiting Confirmation"),
	AWAITING_PWD_RESET("Awaiting Password Reset"), 
	DELETED("Deleted");

	private String description;

	AccountStatus(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}
}
