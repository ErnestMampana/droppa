/**
 * 
 */
package com.droppa.clone.droppa.dto;

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
public class BookingDTO {
	public String userId;

	public int pickUpStandNumber;
	public String pickUpStreetName;
	public String pickUpSuburb;
	public String pickUpProvince;
	public int pickUpPostalCode;

	public int dropOffStandNumber;
	public String dropOffStreetName;
	public String dropOffSuburb;
	public String dropOffProvince;
	public int dropOffPostalCode;

	public String pickUpName;
	public String pickUpSurname;
	public String pickUpContact;

	public String dropOffName;
	public String dropOffSurname;
	public String dropOffContact;

	public String date;

}
