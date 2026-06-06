/**
 * 
 */
package com.droppa.DroppaBookingService.dto;

import jakarta.validation.constraints.NotBlank;
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
public class PaymentDAO {

	@NotBlank
	private String bookingId;
	@NotBlank
	private String paymentType;
	private String usedPromo;

}
