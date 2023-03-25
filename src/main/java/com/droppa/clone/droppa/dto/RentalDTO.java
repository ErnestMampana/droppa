/**
 * 
 */
package com.droppa.clone.droppa.dto;

import java.time.LocalDate;
import com.droppa.clone.droppa.models.Person;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ernest Mampana
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalDTO {

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
}
