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
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Person {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String userName;
	private String surname;
	private String cellphone;
	private double walletBalance;
	private String email;


}
