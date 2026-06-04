/**
 * 
 */
package com.droppa.DroppaBookingService.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Builder;

/**
 * @author Ernest Mampana
 *
 */
@Builder
public record BookingDTO(
	String pickupadress,
	String dropoffadress,
	LocalDate date,
	String vehicle,
	String status,
	String pickUpName,
	String pickUpCellphone,
	String dropOffName,
	String dropOffPhone,
	String paymentType,
	int loads,
	int labours,
	String trackNumber,
	String itemsToBeDelivered,
	BigDecimal bookingPrice,
	String time,
	boolean isPaid
) {
}
