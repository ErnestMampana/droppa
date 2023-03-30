package com.droppa.clone.droppa.enums;

public enum BookingStatus {
	

	COMPLETE("Booking successfully complete"),
	AWAITING_DRIVER("Awaiting driver to be assigned"), 
	IN_TRANSACT("Driver in transit"), 
	INVALID("Booking cancelled"),
	AWAITING_PAYMENT("Awaiting payments"), 
	CANCELLED("Cancelled"), 
	RESERVED("Booking taken already");
	
	private String description;

	BookingStatus(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}
}
