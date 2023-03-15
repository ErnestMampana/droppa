/**
 * 
 */
package com.droppa.clone.droppa.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Company {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String companyId;
	private String companyName;
	@OneToOne
	private Person owner;
	private String location;
}
