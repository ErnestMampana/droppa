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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FleetBookingDTO {
	private String pickupadress;
	private String dropoffadress;
	private String date;
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
	private LocalDate dateTime;
}
