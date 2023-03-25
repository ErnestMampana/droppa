/**
 * 
 */
package com.droppa.clone.droppa.models;

import java.time.LocalDate;

import com.droppa.clone.droppa.enums.RentalStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Rental {

	@Id
	@GeneratedValue
	private int id;
	private String rentalId;
	private String userId;
	private String streetAddress;
	private int postalCode;
	private String suburb;
	private String province;
	private String complexName;
	private int unitNumber;
	private LocalDate startDate;
	private LocalDate endDate;
	private String truckType;
	private double price;
	private String companyName;
	private String contactPerson;
	private String mobileNumber;
	private String rentalBunch;
	private int labours;
	private int noDays;
	private String instruction;
	@Enumerated(EnumType.STRING)
	private RentalStatus status;

}
