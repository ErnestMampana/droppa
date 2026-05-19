package com.droppa.DroppaDriverService.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class PersonClient {
	
	private int id;
	private String userName;
	private String surname;
	private String cellphone;
	private BigDecimal walletBalance;
	private String email;

}
