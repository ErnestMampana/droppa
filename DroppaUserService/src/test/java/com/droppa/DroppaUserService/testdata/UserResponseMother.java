package com.droppa.DroppaUserService.testdata;

import java.math.BigDecimal;

import com.droppa.DroppaUserService.dto.UserResponseDTO;

public class UserResponseMother {
	
	public static UserResponseDTO defaultUser() {
        return UserResponseDTO.builder()
                .email("ernest@gmail.com")
                .token("jwt-token")
                .cellphone("0723568069")
                .userName("ernest")
                .surname("Mohlala")
                .walletBalance(BigDecimal.valueOf(200))
                .build();
    }

    public static UserResponseDTO minimalUser() {
        return UserResponseDTO.builder()
                .email("ernest@gmail.com")
                .token("jwt-token")
                .build();
    }

}
