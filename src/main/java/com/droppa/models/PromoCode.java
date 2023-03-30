/**
 * 
 */
package com.droppa.clone.droppa.models;


import java.time.LocalDate;
import jakarta.persistence.Id;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
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
@Entity
public class PromoCode {	
	@Id
	@GeneratedValue
	private int Id;
	private String promoCode;
	private int numberOfTimesUsed;
	private LocalDate expiration;
	private int promoCount;
	private double discountPrice;
}
