package com.droppa.clone.droppa.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Adress {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private int pickUpStandNumber;
	private String pickUpStreetName;
	private String pickUpSuburb;
	private String pickUpProvince;
	private int pickUpPostalCode;

	private int dropOffStandNumber;
	private String dropOffStreetName;
	private String dropOffSuburb;
	private String dropOffProvince;
	private int dropOffPostalCode;

}
