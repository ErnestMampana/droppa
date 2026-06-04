package com.droppa.DroppaDriverService.entity;

import com.droppa.DroppaDriverService.enums.AccountStatus;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "driver_account")
public class DriverAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false, unique = true)
	private String email;

	private boolean isConfirmed;

	@OneToOne
	private Vehicle vehicle;

	@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, optional = false)
	@JoinColumn(name = "driver_id", nullable = false)
	private VehicleDriver driver;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AccountStatus status;

	public static DriverAccount register(String email, Vehicle vehicle, VehicleDriver driver) {
		DriverAccount account = new DriverAccount();
		account.email = requireText(email, "email");
		account.driver = Objects.requireNonNull(driver, "driver is required");
		account.isConfirmed = false;
		account.status = AccountStatus.AWAITING_CONFIRMATION;
		account.assignVehicle(vehicle);
		return account;
	}

	public void confirm() {
		if (status == AccountStatus.DELETED) {
			throw new IllegalStateException("Deleted driver accounts cannot be confirmed");
		}

		isConfirmed = true;
		status = AccountStatus.ACTIVE;
	}

	public void suspend() {
		if (status == AccountStatus.DELETED) {
			throw new IllegalStateException("Deleted driver accounts cannot be suspended");
		}

		status = AccountStatus.SUSPENDED;
	}

	public void reactivate() {
		if (status != AccountStatus.SUSPENDED) {
			throw new IllegalStateException("Only suspended driver accounts can be reactivated");
		}

		status = AccountStatus.ACTIVE;
	}

	public void assignVehicle(Vehicle vehicle) {
		Vehicle newVehicle = Objects.requireNonNull(vehicle, "vehicle is required");

		if (this.vehicle == newVehicle) {
			return;
		}

		newVehicle.assignDriverAccount(this);

		if (this.vehicle != null) {
			this.vehicle.removeDriverAccount(this);
		}

		this.vehicle = newVehicle;
	}

	private static String requireText(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(fieldName + " is required");
		}

		return value.trim();
	}
}
