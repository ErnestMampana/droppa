package com.droppa.clone.droppa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverDTO {
	
	public String email;
	
	public String password;

	public String name;

	public String surname;

	public long cellphone;

	public String companyId;

	public String registration;
}
