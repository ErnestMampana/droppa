/**
 * 
 */
package com.droppa.clone.droppa.models;

import java.time.LocalDate;

import com.droppa.clone.droppa.enums.BookingStatus;
import com.droppa.clone.droppa.enums.VehicleType;

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
	//@OneToOne
	private String pickUpAddess;
	private String userId;
	//@OneToOne
	private String dropOffAdress;
	private LocalDate bookingDate;
	private double price;
	private String assinedDriver;
	@Enumerated(EnumType.STRING)
	private BookingStatus status;
	private int loads;
	private int labours;
	private String trackNumber;
	private String itemsToBeDelivered;
	//@Enumerated(EnumType.STRING)
	private String vehicleType;
	private String paymentType;
	private String time;
	@OneToOne
	private DropDetails dropDetails;

}
