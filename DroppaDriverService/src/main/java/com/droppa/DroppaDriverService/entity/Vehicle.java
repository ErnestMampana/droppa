package com.droppa.DroppaDriverService.entity;

import java.time.LocalDate;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "vehicle")
public class Vehicle {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false, unique = true)
	private String registration;

	@Column(nullable = false)
	private String make;

	@Column(nullable = false)
	private String type;

	private LocalDate discExpiryDate;

	@OneToOne(mappedBy = "vehicle")
	private DriverAccount driverAccount;

	@ManyToOne
	@JoinColumn(name = "company_id", nullable = false)
	private Company company;

	public static Vehicle register(
			String registration,
			String make,
			String type,
			LocalDate discExpiryDate,
			Company company
	) {
		Vehicle vehicle = new Vehicle();
		vehicle.registration = requireText(registration, "registration");
		vehicle.make = requireText(make, "make");
		vehicle.type = requireText(type, "type");
		vehicle.discExpiryDate = discExpiryDate;
		vehicle.company = Objects.requireNonNull(company, "company is required");
		return vehicle;
	}

	void assignDriverAccount(DriverAccount driverAccount) {
		DriverAccount newDriverAccount = Objects.requireNonNull(driverAccount, "driverAccount is required");

		if (this.driverAccount != null && this.driverAccount != newDriverAccount) {
			throw new IllegalStateException("Vehicle is already assigned to another driver account");
		}

		this.driverAccount = newDriverAccount;
	}

	void removeDriverAccount(DriverAccount driverAccount) {
		if (this.driverAccount == driverAccount) {
			this.driverAccount = null;
		}
	}

	private static String requireText(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(fieldName + " is required");
		}

		return value.trim();
	}
}
