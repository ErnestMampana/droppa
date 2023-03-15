/**
 * 
 */
package com.droppa.clone.droppa.enums;

/**
 * @author Ernest Mampana
 *
 */
public enum VehicleType {
	
	MINI_VAN("NP200,Corsa Utility and Similar"),
	ONE_TON("Toyota Hilux,Nissan NP300 or Similar"),
	ONE_POINT_FIVE_TON("Hyndai H100,Kia K2700"),
	FOUR_TON(""),
	EIGHT_TON("");

	private String description;

	VehicleType(String description) {
		this.description = description;
	}

	public String description() {
		return description;
	}
}
