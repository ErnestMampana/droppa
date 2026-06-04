/**
 * 
 */
package com.droppa.DroppaDriverService.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author Ernest Mampana
 *
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "company")
public class Company {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false, unique = true)
	private String companyId;

	@Column(nullable = false)
	private String companyName;

	@Column(nullable = false)
	private int ownerId;

	@Column(nullable = false)
	private String location;

	public static Company create(String companyId, String companyName, int ownerId, String location) {
		Company company = new Company();
		company.companyId = requireText(companyId, "companyId");
		company.companyName = requireText(companyName, "companyName");
		company.ownerId = ownerId;
		company.location = requireText(location, "location");
		return company;
	}

	private static String requireText(String value, String fieldName) {
		if (value == null || value.isBlank()) {
			throw new IllegalArgumentException(fieldName + " is required");
		}

		return value.trim();
	}

}
