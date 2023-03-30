/**
 * 
 */
package com.droppa.clone.droppa.dto;

import java.time.LocalDate;

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
public class CourierDTO {
	private String pickUpAddress;
	private String dropOffAddress;
	private double price;
	private String vehicleType;
	private LocalDate bookingDate;
	private String pickUpNames;
	private String pickUpContact;
	private String pickUpCompanyName;
	private String pickUpSuburb;
	private String dropOffNames;
	private String dropOffContact;
	private String dropOffCompanyName;
	private String dropOffSuburb;
	private String specialNote;
}
