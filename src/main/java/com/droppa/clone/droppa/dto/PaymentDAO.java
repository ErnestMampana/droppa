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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDAO {

	private String userId;
	private String bookingId;
	private String paymentType;
	private String usedPromo;
	private double bookingPrice;

}
