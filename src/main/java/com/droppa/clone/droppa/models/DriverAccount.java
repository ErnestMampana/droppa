package com.droppa.clone.droppa.models;

import com.droppa.clone.droppa.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class DriverAccount {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String email;
	private String password;
	private boolean isConfirmed;
	@OneToOne
	private Vehicle vehicle;
	@OneToOne
	private VehicleDriver driver;
	@Enumerated(EnumType.STRING)
	private AccountStatus status;



}
