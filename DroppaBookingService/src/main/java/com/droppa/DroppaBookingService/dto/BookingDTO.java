/**
 * 
 */
package com.droppa.DroppaBookingService.dto;

import java.time.LocalDate;

import com.droppa.DroppaBookingService.enums.VehicleType;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

/**
 * @author Ernest Mampana
 *
 */
@Builder
public record BookingDTO(
	@NotBlank
	String pickupadress,
	@NotBlank
	String dropoffadress,
	@NotNull
	@FutureOrPresent
	LocalDate date,
	@NotNull
	VehicleType vehicle,
	@NotBlank
	String pickUpName,
	@NotBlank
	String pickUpCellphone,
	@NotBlank
	String dropOffName,
	@NotBlank
	String dropOffPhone,
	String paymentType,
	@Min(1)
	int loads,
	@PositiveOrZero
	int labours,
	@NotBlank
	String itemsToBeDelivered,
	@NotBlank
	String time
) {
}
