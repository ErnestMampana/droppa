/**
 * 
 */
package com.droppa.clone.droppa.dto;

import java.time.LocalDate;

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
//	public String userId;
//
//	public int pickUpAddress;
////	public String pickUpStreetName;
////	public String pickUpSuburb;
////	public String pickUpProvince;
////	public int pickUpPostalCode;
//
//	public int dropOffAddress;
////	public String dropOffStreetName;
////	public String dropOffSuburb;
////	public String dropOffProvince;
////	public int dropOffPostalCode;
//
//	public String pickUpNameAndSurname;
//	//public String pickUpSurname;
//	public String pickUpContact;
//
//	public String dropOffNameAndSurname;
//	//public String dropOffSurname;
//	public String dropOffContact;
//
//	public LocalDate date;
//	public String time;

	private String userId;
	private String pickupadress;
	private String dropoffadress;
	private LocalDate date;
	private String vehicle;
	private String status;
	private String pickUpName;
	private String pickUpCellphone;
	private String dropOffName;
	private String dropOffPhone;
	private String paymentType;
	private int loads;
	private int labours;
	private String trackNumber;
	private String itemsToBeDelivered;
	private double bookingPrice;
	private String time;

}
