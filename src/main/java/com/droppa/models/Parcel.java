/**
 * 
 */
package com.droppa.clone.droppa.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Ernest Mampana
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Parcel {
	@Id
	@GeneratedValue
	private int id;
	private double mass;
	private double height;
	private double length;
	private double width;

}
