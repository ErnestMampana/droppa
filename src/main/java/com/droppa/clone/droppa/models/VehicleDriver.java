/**
 * 
 */
package com.droppa.clone.droppa.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ernest Mampana
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class VehicleDriver {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String email;
	private String name;
	private String surname;
	private long celphone;
	private byte[] driverLicence;
}
