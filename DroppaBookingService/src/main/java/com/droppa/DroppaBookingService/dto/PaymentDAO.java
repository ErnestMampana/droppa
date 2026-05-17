/**
 * 
 */
package com.droppa.DroppaBookingService.dto;

import java.math.BigDecimal;

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

	private String userId;
	private String bookingId;
	private String paymentType;
	private String usedPromo;
	private BigDecimal bookingPrice;

}
