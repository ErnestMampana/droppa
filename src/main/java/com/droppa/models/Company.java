/**
 * 
 */
package com.droppa.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import com.droppa.DroppaUserService.entity.Person;

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
//	@ElementCollection
//	private List<Vehicle> vehicles;

}
