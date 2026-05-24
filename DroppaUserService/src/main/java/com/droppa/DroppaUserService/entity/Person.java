package com.droppa.DroppaUserService.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.droppa.DroppaUserService.enums.AccountStatus;
import com.droppa.DroppaUserService.enums.Role;
import com.droppa.DroppaUserService.exception.ClientException;
import com.droppa.DroppaUserService.security.CredentialManager;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
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
	private BigDecimal walletBalance = BigDecimal.ZERO;
	private String email;
	

	public static Person create(
			 String userName,
			 String surname,
			 String cellphone,
			 String email

    ) {

        Person person = new Person();

        person.userName = userName;
        person.surname = surname;
        person.cellphone = cellphone;
        person.email = email;

        return person;
    }
	public void creditWallet(BigDecimal amount) {


	    this.walletBalance = this.walletBalance.add(amount);
	}
	
	public void debit(BigDecimal amount) {

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ClientException("Invalid amount");
        }

        if (walletBalance.compareTo(amount) < 0) {
            throw new ClientException("Insufficient balance");
        }

        this.walletBalance = this.walletBalance.subtract(amount);
    }


}
