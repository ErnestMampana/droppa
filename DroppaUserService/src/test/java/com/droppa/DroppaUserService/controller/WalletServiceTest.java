package com.droppa.DroppaUserService.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.droppa.DroppaUserService.dto.LoadWalletRequest;
import com.droppa.DroppaUserService.security.JwtAuthenticationFilter;
import com.droppa.DroppaUserService.service.JwtService;
import com.droppa.DroppaUserService.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = WalletController.class)
@AutoConfigureMockMvc(addFilters = false)
public class WalletServiceTest {
	
	@MockBean
	private JwtService jwtService;
	
	@MockBean
	private JwtAuthenticationFilter authenticationFilter;
	
	@MockBean 
	private WalletService service;
	
	 @Autowired
	 private ObjectMapper objectMapper;
	
	@Autowired
    private MockMvc mockMvc;
	
	@Test
	@DisplayName("Should load wallet successfully")
	void shouldLoadWallet() throws Exception{
		
		LoadWalletRequest loadWalletRequest = new LoadWalletRequest();
		loadWalletRequest.setUsername("ernest@gmail.com");
		loadWalletRequest.setAmount(BigDecimal.valueOf(200));
		
		
		
		when(service.loadWallet(any(LoadWalletRequest.class))).thenReturn(BigDecimal.valueOf(200));
		
		 mockMvc.perform(put("/api/v1/wallet/loadwallet")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(objectMapper.writeValueAsString(loadWalletRequest)))
                 .andExpect(status().isOk())
                 .andExpect(content().string("200"));
		
	}
	

}
