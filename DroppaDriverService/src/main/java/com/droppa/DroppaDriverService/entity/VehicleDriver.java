/**
 * 
 */
package com.droppa.DroppaDriverService.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Ernest Mampana
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class VehicleDriver {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	private String email;
	private String name;
	private String surname;
	private long celphone;

	@Lob
	private byte[] driverLicence;

	public static VehicleDriver create(String email, String name, String surname, long cellphone) {
		VehicleDriver driver = new VehicleDriver();
		driver.email = requireText(email, "email");
		driver.name = requireText(name, "name");
		driver.surname = requireText(surname, "surname");
		driver.celphone = cellphone;
		return driver;
	}

	public String getFullName() {
		return name + " " + surname;
	}

	public void uploadDriverLicence(byte[] driverLicence) {
		this.driverLicence = driverLicence == null ? null : driverLicence.clone();
	}

	private static String requireText(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(fieldName + " is required");
		}

		return value.trim();
	}
}
