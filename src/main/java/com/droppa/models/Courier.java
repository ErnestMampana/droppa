/**
 * 
 */
package com.droppa.clone.droppa.models;

import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
public class Courier {
	@Id
	@GeneratedValue
	private int id;
	private String pickUpAddress;
	private String dropOffAddress;
	private double price;
	private String vehicleType;
	private LocalDate bookingDate;
	private String pickUpNames;
	private String pickUpContact;
	private String pickUpCompanyName;
	private String pickUpSuburb;
	private String dropOffNames;
	private String dropOffContact;
	private String dropOffCompanyName;
	private String dropOffSuburb;
	private String specialNote;
	@OneToOne
	private UserAccount person;
	
}
