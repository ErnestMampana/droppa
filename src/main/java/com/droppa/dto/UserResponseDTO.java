/**
 * 
 */
package com.droppa.clone.droppa.dto;

import java.util.List;

import com.droppa.clone.droppa.models.Booking;

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
public class UserResponseDTO {

	private String email;
	private String userName;
	private String surname;
	private String cellphone;
	private Double walletBalance;
	private List<Booking> myBookings;
	private String token;
}
