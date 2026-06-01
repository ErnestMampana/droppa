package com.droppa.DroppaUserService.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;

import com.droppa.DroppaUserService.dto.ConfirmEmailRequest;
import com.droppa.DroppaUserService.dto.CredentialsDTO;
import com.droppa.DroppaUserService.dto.PasswordResetRequest;
import com.droppa.DroppaUserService.dto.PersonDTO;
import com.droppa.DroppaUserService.dto.ResetPasswordRequest;
import com.droppa.DroppaUserService.dto.UserResponseDTO;
import com.droppa.DroppaUserService.security.JwtAuthenticationFilter;
import com.droppa.DroppaUserService.service.AuthenticationService;
import com.droppa.DroppaUserService.service.AuthenticationServiceTest;
import com.droppa.DroppaUserService.service.JwtService;
import com.droppa.DroppaUserService.testdata.RequestMother;
import com.droppa.DroppaUserService.testdata.UserResponseMother;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

	@Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthenticationService service;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;


    @Test
    @DisplayName("Should register user successfully")
    void shouldRegisterUserSuccessfully() throws Exception {

        var request = RequestMother.validPerson();

        when(service.createUserAccount(any(PersonDTO.class)))
                .thenReturn(UserResponseMother.defaultUser());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ernest@gmail.com"))
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.cellphone").value("0723568069"));
    }

    @Test
    @DisplayName("Should authenticate user successfully")
    void shouldAuthenticateUserSuccessfully() throws Exception {

        var request = RequestMother.validCredentials();

        when(service.authenticate(any(CredentialsDTO.class)))
                .thenReturn(UserResponseMother.defaultUser());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ernest@gmail.com"))
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }


    @Test
    @DisplayName("Should confirm user account with otp")
    void shouldConfirmEmail() throws Exception {

        var request = RequestMother.confirmEmail();

        when(service.confirmEmail(any(ConfirmEmailRequest.class)))
                .thenReturn(UserResponseMother.defaultUser());

        mockMvc.perform(post("/api/v1/auth/confirm-email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ernest@gmail.com"));
    }


    @Test
    @DisplayName("Should request password reset successfully")
    void shouldRequestPasswordReset() throws Exception {

        var request = RequestMother.passwordReset();

        when(service.requestPasswordReset(request.getEmail()))
                .thenReturn("12345");

        mockMvc.perform(post("/api/v1/auth/password-reset/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("12345"));
    }



    @Test
    @DisplayName("Should reset password successfully")
    void shouldResetPassword() throws Exception {

        var request = RequestMother.resetPassword();

        when(service.resetPassword(any(ResetPasswordRequest.class)))
                .thenReturn(UserResponseMother.defaultUser());

        mockMvc.perform(put("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ernest@gmail.com"));
    }
    
    
    
}