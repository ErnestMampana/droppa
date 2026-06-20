package com.droppa.DroppaDriverService.entity;

import com.droppa.DroppaDriverService.enums.AccountStatus;
import com.droppa.DroppaDriverService.enums.DriverAvailability;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DriverAccountTest {

	@Test
	void registerCreatesAwaitingConfirmationAccount() {
		DriverAccount account = newDriverAccount();

		assertEquals("driver@example.com", account.getEmail());
		assertFalse(account.isConfirmed());
		assertEquals(AccountStatus.AWAITING_CONFIRMATION, account.getStatus());
		assertEquals(DriverAvailability.OFFLINE, account.getAvailabilityStatus());
		assertEquals("Driver User", account.getDriver().getFullName());
		assertSame(account, account.getVehicle().getDriverAccount());
	}

	@Test
	void confirmActivatesAccount() {
		DriverAccount account = newDriverAccount();

		account.confirm();

		assertTrue(account.isConfirmed());
		assertEquals(AccountStatus.ACTIVE, account.getStatus());
		assertEquals(DriverAvailability.OFFLINE, account.getAvailabilityStatus());
	}

	@Test
	void availabilityTracksOnlineAndDeliveryState() {
		DriverAccount account = newDriverAccount();
		account.confirm();

		account.goOnline();

		assertEquals(DriverAvailability.ONLINE, account.getAvailabilityStatus());
		assertTrue(account.isOnlineForOffers());

		account.startTransit();

		assertEquals(DriverAvailability.IN_TRANSIT, account.getAvailabilityStatus());
		assertFalse(account.isOnlineForOffers());
		assertThrows(IllegalStateException.class, account::goOffline);

		account.completeTransit();

		assertEquals(DriverAvailability.ONLINE, account.getAvailabilityStatus());
	}

	@Test
	void reactivateRequiresSuspendedAccount() {
		DriverAccount account = newDriverAccount();

		assertThrows(IllegalStateException.class, account::reactivate);
	}

	@Test
	void assignVehicleUpdatesVehicleAssociation() {
		DriverAccount account = newDriverAccount();
		Vehicle originalVehicle = account.getVehicle();
		Vehicle replacementVehicle = newVehicle("DRV-002");

		account.assignVehicle(replacementVehicle);

		assertSame(replacementVehicle, account.getVehicle());
		assertNull(originalVehicle.getDriverAccount());
		assertSame(account, replacementVehicle.getDriverAccount());
	}

	@Test
	void assignVehicleRejectsVehicleAlreadyAssignedToAnotherAccount() {
		DriverAccount firstAccount = newDriverAccount();
		DriverAccount secondAccount = DriverAccount.register(
				"second-driver@example.com",
				newVehicle("DRV-002"),
				VehicleDriver.create(
						"second-driver@example.com",
						"Second",
						"Driver",
						27821234568L
				)
		);
		Vehicle firstVehicle = firstAccount.getVehicle();

		assertThrows(IllegalStateException.class, () -> firstAccount.assignVehicle(secondAccount.getVehicle()));
		assertSame(firstVehicle, firstAccount.getVehicle());
		assertSame(firstAccount, firstVehicle.getDriverAccount());
		assertSame(secondAccount, secondAccount.getVehicle().getDriverAccount());
	}

	private DriverAccount newDriverAccount() {
		VehicleDriver driver = VehicleDriver.create(
				"driver@example.com",
				"Driver",
				"User",
				27821234567L
		);

		return DriverAccount.register(
				"driver@example.com",
				newVehicle("DRV-001"),
				driver
		);
	}

	private Vehicle newVehicle(String registration) {
		Company company = Company.create("COMP-" + registration, "Driver Company", 1, "Johannesburg");
		return Vehicle.register(registration, "Toyota", "Bakkie", null, company);
	}
}
