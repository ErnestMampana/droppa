/**
 * 
 */
package com.droppa.clone.droppa.models;


import com.droppa.clone.droppa.enums.BookingStatus;
import jakarta.persistence.*;
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
@Entity
public class Booking {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String bookingId;
	@OneToOne
	private Adress adressDetails;
	private String userId;
	@OneToOne
	private DropDetails dropDetails;
	private String bookingDate;
	private double price;
	private String assinedDriver;
	@Enumerated(EnumType.STRING)
	private BookingStatus status;

}
