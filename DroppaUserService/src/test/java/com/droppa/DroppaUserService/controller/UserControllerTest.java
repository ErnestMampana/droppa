package com.droppa.DroppaUserService.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import com.droppa.DroppaUserService.entity.Person;
import com.droppa.DroppaUserService.entity.UserAccount;
import com.droppa.DroppaUserService.enums.Role;
import com.droppa.DroppaUserService.security.JwtAuthenticationFilter;
import com.droppa.DroppaUserService.service.JwtService;
import com.droppa.DroppaUserService.service.UserService;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {
	
	@Autowired
    private MockMvc mockMvc;

	@MockBean
	private JwtService jwtService;

    @MockBean
    private UserService userService;
    
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;
	
    @Test
    @DisplayName("Should get user by email")
    void shouldGetUserByEmail() throws Exception {

        Person person = Person.create(
                "Ernest",
                "Mohlala",
                "0723568069",
                "ernest@gmail.com"
        );

        UserAccount userAccount = UserAccount.createPendingAccount(
                "ernest@gmail.com",
                "encoded-password",
                person,
                Role.USER,
                "12345",
                LocalDateTime.now().plusMinutes(10)
        );

        when(userService.getUserByEmail("ernest@gmail.com"))
                .thenReturn(userAccount);

        mockMvc.perform(get("/api/v1/user/getuserbyemail/{email}", "ernest@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ernest@gmail.com"))
                .andExpect(jsonPath("$.surname").value("Mohlala"))
                .andExpect(jsonPath("$.cellphone").value("0723568069"));
    }

}
