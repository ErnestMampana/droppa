package com.droppa.DroppaDriverService.services;

import com.droppa.DroppaDriverService.dto.DriverDTO;
import com.droppa.DroppaDriverService.dto.PersonClient;
import com.droppa.DroppaDriverService.dto.UserAccountClient;
import com.droppa.DroppaDriverService.entity.Company;
import com.droppa.DroppaDriverService.entity.DriverAccount;
import com.droppa.DroppaDriverService.entity.Vehicle;
import com.droppa.DroppaDriverService.enums.AccountStatus;
import com.droppa.DroppaDriverService.exception.ClientException;
import com.droppa.DroppaDriverService.interfaces.UserServiceClient;
import com.droppa.DroppaDriverService.repositories.DriverAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DriverServiceTest {

	private FakeDriverAccountRepository driverAccountRepository;
	private FakeVehicleService vehicleService;
	private FakeUserServiceClient userServiceClient;
	private DriverService driverService;

	@BeforeEach
	void setUp() {
		driverAccountRepository = new FakeDriverAccountRepository();
		vehicleService = new FakeVehicleService();
		userServiceClient = new FakeUserServiceClient();
		driverService = new DriverService(driverAccountRepository.proxy(), vehicleService, userServiceClient);
	}

	@Test
	void createDriverAccountRejectsUnconfirmedUser() {
		DriverDTO driverDto = newDriverDto();
		userServiceClient.account = new UserAccountClient(1, "driver@example.com", false, "ACTIVE");

		ClientException exception = assertThrows(
				ClientException.class,
				() -> driverService.createDriverAccount(driverDto, " driver@example.com ")
		);

		assertEquals("Only confirmed active users can register as drivers", exception.getMessage());
		assertEquals("driver@example.com", userServiceClient.requestedEmail);
		assertNull(userServiceClient.requestedPersonEmail);
		assertEquals(0, vehicleService.calls);
		assertEquals(0, driverAccountRepository.existsByEmailCalls);
		assertNull(driverAccountRepository.savedAccount);
	}

	@Test
	void createDriverAccountRegistersConfirmedActiveUserWithProfileFromUserService() {
		DriverDTO driverDto = newDriverDto();
		vehicleService.vehicle = Vehicle.register(
				"DRV-001",
				"Toyota",
				"Bakkie",
				null,
				Company.create("COMP-001", "Driver Company", 1, "Johannesburg")
		);
		userServiceClient.account = new UserAccountClient(1, "driver@example.com", true, "ACTIVE");
		userServiceClient.person = newPerson(
				"driver@example.com",
				"ProfileName",
				"ProfileSurname",
				"+27 82-123-4567"
		);
		driverAccountRepository.existsByEmail = false;

		DriverAccount account = driverService.createDriverAccount(driverDto, "driver@example.com");

		assertEquals("driver@example.com", account.getEmail());
		assertEquals("ProfileName", account.getDriver().getName());
		assertEquals("ProfileSurname", account.getDriver().getSurname());
		assertEquals(27821234567L, account.getDriver().getCelphone());
		assertFalse(account.isConfirmed());
		assertEquals(AccountStatus.AWAITING_CONFIRMATION, account.getStatus());
		assertSame(vehicleService.vehicle, account.getVehicle());
		assertSame(account, vehicleService.vehicle.getDriverAccount());
		assertSame(account, driverAccountRepository.savedAccount);
		assertEquals("DRV-001", vehicleService.requestedRegistration);
		assertEquals("driver@example.com", userServiceClient.requestedPersonEmail);
	}

	private DriverDTO newDriverDto() {
		return DriverDTO.builder()
				.registration("DRV-001")
				.build();
	}

	private PersonClient newPerson(String email, String userName, String surname, String cellphone) {
		PersonClient person = new PersonClient();
		person.setEmail(email);
		person.setUserName(userName);
		person.setSurname(surname);
		person.setCellphone(cellphone);
		return person;
	}

	private static final class FakeUserServiceClient implements UserServiceClient {
		private UserAccountClient account;
		private PersonClient person;
		private String requestedEmail;
		private String requestedPersonEmail;

		@Override
		public PersonClient getUserByEmail(String email) {
			requestedPersonEmail = email;
			return person;
		}

		@Override
		public UserAccountClient getUserAccountByEmail(String email) {
			requestedEmail = email;
			return account;
		}
	}

	private static final class FakeVehicleService extends VehicleService {
		private Vehicle vehicle;
		private String requestedRegistration;
		private int calls;

		private FakeVehicleService() {
			super(null, null);
		}

		@Override
		public Vehicle getVehicleByRegistration(String vehicleReg) {
			calls++;
			requestedRegistration = vehicleReg;
			return vehicle;
		}
	}

	private static final class FakeDriverAccountRepository implements InvocationHandler {
		private boolean existsByEmail;
		private int existsByEmailCalls;
		private DriverAccount savedAccount;

		private DriverAccountRepository proxy() {
			return (DriverAccountRepository) Proxy.newProxyInstance(
					DriverAccountRepository.class.getClassLoader(),
					new Class<?>[]{DriverAccountRepository.class},
					this
			);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) {
			if (method.getDeclaringClass() == Object.class) {
				return switch (method.getName()) {
					case "toString" -> "FakeDriverAccountRepository";
					case "hashCode" -> System.identityHashCode(proxy);
					case "equals" -> proxy == args[0];
					default -> throw new UnsupportedOperationException(method.getName());
				};
			}

			return switch (method.getName()) {
				case "existsByEmail" -> {
					existsByEmailCalls++;
					yield existsByEmail;
				}
				case "save" -> {
					savedAccount = (DriverAccount) args[0];
					yield savedAccount;
				}
				default -> throw new UnsupportedOperationException(method.getName());
			};
		}
	}
}
