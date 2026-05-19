/**
 * 
 */
package com.droppa.DroppaDriverService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


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
//	@OneToOne
	private int ownerId;
	private String location;
//	@ElementCollection
//	private List<Vehicle> vehicles;

}
