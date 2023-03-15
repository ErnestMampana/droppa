package com.droppa.clone.droppa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonDTO {
	public String userName;
	public String surname;
	public String cellphone;
	public String email;
	public String password;
}
